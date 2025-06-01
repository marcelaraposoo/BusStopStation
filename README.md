# BusStopStation 🚌

Este projeto foi desenvolvido para a disciplina de **Infraestrutura de Software** (2024.2), com o objetivo de aplicar conceitos de **concorrência** em Java. A simulação representa um ponto de ônibus onde passageiros chegam, ônibus passam (ou param, se tiverem que parar) e semáforos controlam o fluxo dos veículos.

-----------

🛠️  **Entrega 1 — Setup Inicial**

- Interface gráfica funcionando (com Play e Stop).
- Thread para gerar ônibus a cada 4 segundos.
- Cada ônibus tem limite de 20 passageiros.
- Ônibus aparecem na tela, se movem e somem no final da tela.

---------
🚦 **Entrega 2 — Semáforo e Pessoas**
Implementação do funcionamento do semáforo:

- 🟢 Verde e 🟡 Amarelo: ônibus passam direto.

- 🔴 Vermelho: ônibus param.

- Thread para criação de pessoas a cada 1.2 segundos.

- Pessoas aparecem e ficam esperando no ponto (se estiver no modo Play).

- Controle de criação pausado/retomado pelos botões.
----------


🤝 **Entrega 3 — Concorrência**


- Ônibus param no ponto se:

> Tiverem espaço livre.

> Tiverem pessoas esperando.

> E não tiver outro ônibus parado.

- Passageiros embarcam no ônibus até:

> Preencher a lotação.

> Ou não restarem mais pessoas na fila.

- Passageiros embarcados desaparecem da tela (e da lista de espera).

- Controle completo da concorrência, evitando:

>Condições de corrida.

>Acesso indevido.

>Problemas de sincronização.

--------------

## Este projeto foi desenvolvido por:

- Adriana Melcop | atmc@cin.ufpe.br
- Marcela Raposo | mpr@cin.ufpe.br
