import support.Bus;
import support.BusStopWindow;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class BusStopSimulator {
    private BusStopWindow window;
    private boolean running = false;

    // Lista de onibus
    private final List<Bus> busList = new ArrayList<>();

    private Thread busCreatorThread;

    // Listener para o botao Play: inicia a simulação
    private final ActionListener buttonListenerPlayPause = e -> {
        if (!running) {
            running = true;
            window.setEnabledPlayButton(false);
            window.setEnabledStopButton(true);
            startSimulation();
        }
    };

    // Listener para o botão Stop: interrompe a simulação
    private final ActionListener buttonListenerStop = e -> {
        running = false;
        window.setEnabledPlayButton(true);
        window.setEnabledStopButton(false);
        stopSimulation();
    };

    public BusStopSimulator() {
        // Inicializa a interface gráfica na Event Dispatch Thread (EDT)
        EventQueue.invokeLater(() ->
                window = new BusStopWindow("BUS STOP", buttonListenerPlayPause, buttonListenerStop)
        );
    }

    /**
     * Inicia a simulação: cria uma thread que, a cada 4 segundos, cria um novo ônibus.
     * Cada ônibus é inicializado com um limite de 20 passageiros e adicionado à lista.
     * Cada ônibus possui sua própria thread que o movimenta; ao alcançar o fim da tela,
     * o ônibus é removido da interface e da lista.
     */
    private void startSimulation() {
        busCreatorThread = new Thread(() -> {
            while (running) {
                // Cria um novo ônibus com limite de 20 passageiros
                Bus bus = window.createBus(20);

                // Adiciona o ônibus à lista
                synchronized(busList) {
                    busList.add(bus);
                }

                // Para cada ônibus, inicia uma thread que cuida da movimentação
                new Thread(() -> {
                    // Inicia a animação (o metodo startBus() já chama stepBus() uma vez e inicia o timer de animação)
                    bus.startBus();
                    // Loop de movimentação: chama stepBus() repetidamente até o ônibus alcançar o final da tela
                    while (running && !bus.reachedEndOfScreen()) {
                        bus.stepBus();
                    }
                    // Quando o ônibus atinge o final da tela, remove-o da interface
                    window.removeBus(bus);
                    // Remove o ônibus da lista
                    synchronized(busList) {
                        busList.remove(bus);
                    }
                }).start();

                try {
                    // Aguarda 4 segundos antes de criar o próximo ônibus
                    Thread.sleep(4000);
                } catch (InterruptedException ex) {
                    break;
                }
            }
        });
        busCreatorThread.start();
    }

    /**
     * Interrompe a simulação, interrompendo a thread de criação de ônibus.
     */
    private void stopSimulation() {
        if (busCreatorThread != null) {
            busCreatorThread.interrupt();
        }
    }
}
