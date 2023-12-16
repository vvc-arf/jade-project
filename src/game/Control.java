//package game;
//
//import jade.core.AID;
//import jade.core.Agent;
//import jade.core.behaviours.Behaviour;
//import jade.core.behaviours.CyclicBehaviour;
//import jade.core.behaviours.OneShotBehaviour;
//import jade.domain.DFService;
//import jade.domain.FIPAAgentManagement.DFAgentDescription;
//import jade.domain.FIPAAgentManagement.ServiceDescription;
//import jade.domain.FIPAException;
//import jade.lang.acl.ACLMessage;
//import jade.lang.acl.MessageTemplate;
//
//import java.util.ArrayList;
//
//public class Control extends Agent {
//    private ArrayList<AID> commonDroneAgents;
//
//    @Override
//    protected void setup() {
//        System.out.printf("Агент-узел управления %s запущен.%n", getAID().getName());
//
//        // Получаем аргументы - координаты квадрата (например 0 25 = (0,0) (25, 25))
//        Object[] args = getArguments();
//        if (args != null && args.length > 0) {
//            startCoordinate = Integer.parseInt((String) args[0]);
//            endCoordinate = Integer.parseInt((String) args[1]);
//            System.out.printf("Агент действует в квадрате %d, %d.%n", startCoordinate, endCoordinate);
//
//            // Получаем всех агентов-военных (для справки)
//            getAllMilitaryAgents();
//            addBehaviour(new Drone.SequenceMoveServer());
//        } else {
//            // Уничтожаем агента
//            System.out.println("Агенты-военные отсутствуют."); // добавляем здесь логику передачи другим дронам и узлу управления
//            doDelete();
//        }
//    }
//
//    private void getAllMilitaryAgents() {
//        addBehaviour(new CyclicBehaviour() {
//            @Override
//            public void action() {
//                // Обновляем список агентов-военных
//                DFAgentDescription template = new DFAgentDescription();
//                ServiceDescription sd = new ServiceDescription();
//                sd.setType("show");
//                template.addServices(sd);
//                try {
//                    DFAgentDescription[] result = DFService.search(myAgent, template);
//                    System.out.println("Найдены следующие агенты-военные в DF:");
//                    commonMilitaryAgents = new AID[result.length];
//                    for (int i = 0; i < result.length; ++i) {
//                        commonMilitaryAgents[i] = result[i].getName();
//                        System.out.println(commonMilitaryAgents[i].getName());
//                    }
//                } catch (FIPAException fe) {
//                    fe.printStackTrace();
//                }
//            }
//        });
//    }
//
//    @Override
//    protected void takeDown() {
//        System.out.printf("Агент-узел управления %s уничтожен.%n", getAID().getName());
//    }
//
//    private class SequenceMoveServer extends Behaviour {
//        @Override
//        public void action() {
//            System.out.println("Поиск военных в заданном квадрате..."); // потом поиск в заданном квадрате
//            for (int i = startCoordinate; i <= endCoordinate; i++) {
//                for (int j = startCoordinate; j <= endCoordinate; j++) {
//                    for (AID militaryAgent : commonMilitaryAgents) {
//                        // Отправляем сообщение
//                        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
//                        msg.addReceiver(militaryAgent);
//                        msg.setContent(String.format("%d,%d", i, j));
//                        send(msg);
//
//                        addBehaviour(new CyclicBehaviour(Drone.this) {
//                            public void action() {
//                                // Получаем ответ
//                                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
//                                ACLMessage reply = myAgent.receive(mt);
//                                if (reply != null && reply.getContent().equals("True") && !ownMilitaryAgents.contains(militaryAgent)) {
//                                    System.out.printf("Дрон ---> Обнаружен агент-военный %s.%n", militaryAgent.getName());
//                                    ownMilitaryAgents.add(militaryAgent);
//                                }
//                                block();
//                            }
//                        });
//                    }
//                }
//            }
//        }
//
//        @Override
//        public boolean done() {
//            System.out.printf("Агент-дрон %s закончил работу.%n", getAID().getName());
//            // done когда найден дрон либо когда клетки закончились
//            return true;
//        }
//    }
//}
