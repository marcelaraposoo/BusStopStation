import support.Bus;
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
    // variáveis de região crítica (comum a todas as threads)
    private BusStopWindow window;
    // "running" indica se a simulação está ativa e "paused" indica se a criação de ônibus está pausada
    private final boolean running = true;
    private boolean paused = false;

    private final List<Bus> busList = new ArrayList<>();
    private final Lock lock = new ReentrantLock();
    private final Condition simulationCondition = lock.newCondition();
    private final Semaphore busCreationSemaphore = new Semaphore(1);

    // Listener para o botão de play/resume: retoma a criação de ônibus.
    private final ActionListener buttonListenerPlayPause = e -> {
        lock.lock();
        try {
            paused = false; // Retoma a criação de novos ônibus
            simulationCondition.signalAll(); // Acorda a thread de criação que estava aguardando
        } finally {
            lock.unlock();
        }
        window.setEnabledPlayButton(false);
        window.setEnabledStopButton(true);
        startSimulation();
    };

    // Listener para o botão de stop: interrompe a criação de novos ônibus, mas não afeta os que estão na tela
    private final ActionListener buttonListenerStop = e -> {
        lock.lock();
        try {
            paused = true; // Pausa apenas a criação de novos ônibus
        } finally {
            lock.unlock();
        }
        window.setEnabledPlayButton(true);
        window.setEnabledStopButton(false);
    };

    public BusStopSimulator() {
        // inicializa a interface gráfica na EDT
        EventQueue.invokeLater(() ->
                window = new BusStopWindow("BUS STOP", buttonListenerPlayPause, buttonListenerStop)
        );
    }

    private void startSimulation() {
        new Thread(() -> {
            while (running) {
                // verifica se a criação de ônibus está pausada antes de criar um novo
                lock.lock();
                try {
                    while (paused) {
                        simulationCondition.await();
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock();
                }
                try {
                    busCreationSemaphore.acquire();
                    EventQueue.invokeLater(this::createBus);
                    // delay para a criação dos ônibus (4000ms)
                    sleep(4000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } finally {
                    busCreationSemaphore.release();
                }
            }
        }).start();
    }

    private void createBus() {
        // verifica se a criação de ônibus está pausada antes de criar um novo
        lock.lock();
        try {
            if (paused) {
                return;
            }
        } finally {
            lock.unlock();
        }
        // cria novo ônibus com capacidade para 20 passageiros
        Bus bus = window.createBus(20);
        // adiciona o ônibus à lista com proteção
        lock.lock();
        try {
            busList.add(bus);
        } finally {
            lock.unlock();
        }
        // cria uma thread para movimentar o ônibus
        moveBus(bus);
    }

    private void moveBus(Bus bus) {
        new Thread(() -> {
            // inicia a animação; assume que startBus() inicia o timer interno
            bus.startBus();
            // o laço de movimentação não verifica mais a condição de pausa.
            // assim, os ônibus já na tela continuam se movendo normalmente, independentemente do estado "paused".
            while (!bus.reachedEndOfScreen()) {
                bus.stepBus();
                try {
                    sleep(1);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
            // quando o ônibus atinge o fim da tela, remove-o na EDT
            EventQueue.invokeLater(() -> {
                lock.lock();
                try {
                    busList.remove(bus);
                    window.removeBus(bus);
                } finally {
                    lock.unlock();
                }
            });
        }).start();
    }
}