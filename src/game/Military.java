package game;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.Collections;

public class Military extends Agent {
    private Position position;

    // Инициализация агента
    @Override
    protected void setup() {
        System.out.printf("Агент-военный %s запущен.%n", getAID().getName());
        position = Position.randomPosition();

        // Регистрируем сервис в DF
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("show");
        sd.setName("show position");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        // Добавляем поведение рандомного движения
        addBehaviour(new RandomMoveServer(this, 2000));

        // Добавляем поведение отправки координат
        addBehaviour(new PositionRequestServer());
    }

    // Очищаем агента
    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        System.out.printf("Агент-военный %s уничтожен.%n", getAID().getName());
    }

    // Случайная новая позиция
    public void randomMove() {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                ArrayList<Position> moveList = new ArrayList<>();
                moveList.add(new Position(-1, 0));
                moveList.add(new Position(1, 0));
                moveList.add(new Position(0, -1));
                moveList.add(new Position(0, 1));
                Collections.shuffle(moveList);
                for (Position move : moveList) {
                    if (position.isOutOfBorder(move)) {
                        position.add(move);
                        System.out.printf("Агент-военный переместился на позицию %s.%n", position.toStringForOutput());
                        break;
                    }
                }
            }
        });
    }

    /**
     * Получаем Request на получение координат.
     * Отдаём Inform с координатами.
     */
    private class PositionRequestServer extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                // Получаем сообщение Request (Текущие координаты дрона)
                String content = msg.getContent();
                ACLMessage reply = msg.createReply();

                if (content != null) {
                    // Если координаты дрона равны координатам военного,
                    // то отправляем дрону ответ True, иначе False
                    String[] dronePositionAsString = content.split(",");
                    Position dronePosition = new Position(
                            Integer.parseInt(dronePositionAsString[0]),
                            Integer.parseInt(dronePositionAsString[1])
                    );
                    if (position.isEqualTo(dronePosition)) {
                        // Информируем
                        reply.setPerformative(ACLMessage.INFORM);
                        reply.setContent("True");
                    }
//                    else  {
//                        reply.setPerformative(ACLMessage.INFORM);
//                        reply.setContent("False");
//                    }
                } else {
                    reply.setPerformative(ACLMessage.REFUSE);
                    reply.setContent("Контент не доступен");
                }
                myAgent.send(reply);
            } else {
                block();
            }
        }
    }  // End of inner class PositionRequestServer

    private class RandomMoveServer extends TickerBehaviour {
        public RandomMoveServer(Agent agent, long period) {
            super(agent, period);
        }

        @Override
        protected void onTick() {
            ((Military) myAgent).randomMove();
        }
    }  // End of inner class RandomMoveServer
}
