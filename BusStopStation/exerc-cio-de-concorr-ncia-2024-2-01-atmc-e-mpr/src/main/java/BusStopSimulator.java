import support.Bus;Add commentMore actions
import support.Person;
import support.BusStopWindow;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import static java.lang.Thread.sleep;

@SuppressWarnings({"BusyWait", "CallToPrintStackTrace"})

public class BusStopSimulator {
    // variáveis de região critica geral (comum a todas as threads)
    private List<Person> pessoas = null;
    private List<Person> peopleOnStop = new ArrayList<>();
    private List<Bus> buses = null;
    private BusStopWindow window;
    private int peopleOnBusStop = 0;
    private int busOnStop = 0;
    private boolean running = false;

    private final ActionListener buttonListenerPlayPause = e -> {


    };

    private final ActionListener buttonListenerStop = e -> {


    };

    public BusStopSimulator() {
        EventQueue.invokeLater(() -> window = new BusStopWindow(
                "BUS STOP",
                buttonListenerPlayPause,
                buttonListenerStop
        ));
    }
}