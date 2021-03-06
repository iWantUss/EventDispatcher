package eventdispatcher;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

class Event{
    private String type;

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

interface ImplEventDispatcher{
    /**
     * Добавляет событие и его обработчик в хранилище
     * @param typeEvent тип события
     * @param actionEvent обработчик события
     */
    void addEventLisenter(String typeEvent, Consumer<Event> actionEvent);
    /**
     * Удаляет обработчик события
     * @param typeEvent тип события
     * @param actionEvent обработчик события
     * @throws NullPointerException выбросит, если в хранилище нет такого типа события
     */
    void removeEventLisenter(String typeEvent, Consumer<Event> actionEvent) throws NullPointerException;
    /**
     * Выполняет все обработчики данного события
     * @param event 
     * @throws NullPointerException выбрасывает, если нет событий с таким типом
     */
    void dispatch(Event event) throws NullPointerException;
}

/**
 * Обработка {@see Event событий}
 */
class EventDispatcher implements ImplEventDispatcher {
    
    /**
     * Хранит все события, которые нужно исполнить
     */
    private static Map<String, Collection<Consumer<Event>>> events = new ConcurrentHashMap<>();

   
    
    @Override
    public void addEventLisenter(String typeEvent, Consumer<Event> actionEvent) {
        Collection<Consumer<Event>> actionsEvent = events.get(typeEvent);
        
        if(actionsEvent!=null){
            actionsEvent.add(actionEvent);
        } else {
            
            actionsEvent = new HashSet<>();
            actionsEvent.add(actionEvent);
            
            events.put(typeEvent, actionsEvent);
        }
    }

    @Override
    public void removeEventLisenter(String typeEvent, Consumer<Event> actionEvent) throws NullPointerException {
        Collection<Consumer<Event>> actionsEvent = events.get(typeEvent);
        
            actionsEvent.remove(actionEvent);
            if(actionsEvent.isEmpty())
                events.remove(typeEvent);
        
        
    }
    /**
     * Вызывает все Consumer с типом Event
     * @param event 
     * @throws NullPointerException выбрасывает, если нет событий с таким типом
     */
    @Override
    public void dispatch(Event event) throws NullPointerException {
        Collection<Consumer<Event>> actionsEvent = new HashSet();
        actionsEvent.addAll(events.get(event.getType()));
        
        actionsEvent.forEach((consumer) -> {
            consumer.accept(event);
        });
    }
    
     /**
      * Имитируем работу с классом {@see EventDispatcher}
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        EventDispatcher IED = new EventDispatcher();
        
        Consumer<Event> purchase1 = (Event event) -> {
            System.out.println("Пользователь 1 что-то купил");
        };
        
        IED.addEventLisenter("login", (Event event) -> {
            System.out.println("Пользователь 1 зашел в систему");
        });
        IED.addEventLisenter("purchase",purchase1);
        IED.addEventLisenter("login", (Event event) -> {
            System.out.println("Пользователь 2 зашел в систему");
        });
        IED.addEventLisenter("logout", (Event event) -> {
            System.out.println("Пользователь 2 Вышел из системы");
        });
        IED.addEventLisenter("logout", (Event event) -> {
            System.out.println("Пользователь 1 Вышел из системы");
        });
        System.out.println("-------login-------");
        Event event = new Event();
        event.setType("login");
        IED.dispatch(event);
        
        
        System.out.println("-------logout-------");
        event.setType("logout");
        IED.dispatch(event);
        
        
        System.out.println("-------purchase-------");
        event.setType("purchase");
        IED.dispatch(event);
        
        IED.removeEventLisenter("purchase", purchase1);
        
        System.out.println("-------purchase-------");
        event.setType("purchase");
        try{
        IED.dispatch(event);
        } catch (NullPointerException npe){
            System.out.println("Нет события с типом purchase");
        }
    }
}
