package com.BlackJack.BlackJackFX;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.animation.PauseTransition;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.concurrent.atomic.AtomicInteger;

public class blackJack {

    private DeckOfCards deck;
    private Dealer dealer;
    private Jogador jogador1;
    private JogadorIA ia;
    private  String iaRespoista;

    public blackJack(DeckOfCards deck, Dealer dealer, Jogador jogador1) {
        this.deck = deck;
        this.dealer = dealer;
        this.jogador1 = jogador1;
        this.deck.shuffle();
    }

    public blackJack(DeckOfCards deck, Dealer dealer, Jogador jogador1, JogadorIA ia) {
        this.deck = deck;
        this.dealer = dealer;
        this.jogador1 = jogador1;
        this.ia=ia;
        this.deck.shuffle();
    }

    public void startGUI(Stage stage) {

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        Pane jogoLayout = new Pane(); // Pane permite posicionamento livre
        jogoLayout.getStyleClass().add("jogoLayout"); // CSS com a imagem da mesa

        // Dealer
        HBox dealerCartas = new HBox(15);
        dealerCartas.getStyleClass().add("dealerCartas");
        dealerCartas.layoutXProperty().bind(jogoLayout.widthProperty().multiply(0.35)); // 50% da largura
        dealerCartas.layoutYProperty().bind(jogoLayout.heightProperty().multiply(0.43)); // ajuste para seu layout

        // Jogador
        HBox jogadorCartas = new HBox(10);
        jogadorCartas.layoutXProperty().bind(jogoLayout.widthProperty().multiply(0.28)); // ajuste para seu layout
        jogadorCartas.layoutYProperty().bind(jogoLayout.heightProperty().multiply(0.73));


        HBox iaCartas = new HBox(10);
        iaCartas.layoutXProperty().bind(jogoLayout.widthProperty().multiply(0.05)); // ajuste para seu layout
        iaCartas.layoutYProperty().bind(jogoLayout.heightProperty().multiply(0.43));


        // Botões Hit/Stand
        Button hit = new Button("Hit");
        Button stand = new Button("Stand");
        hit.getStyleClass().add("botao");
        stand.getStyleClass().add("botao");

        HBox botoes = new HBox(20, hit, stand);
        botoes.layoutXProperty().bind(jogoLayout.widthProperty().multiply(0.7));
        botoes.layoutYProperty().bind(jogoLayout.heightProperty().multiply(1.08));

        jogoLayout.getChildren().addAll(dealerCartas, jogadorCartas,iaCartas, botoes);

        Scene jogoScene = new Scene(jogoLayout, screenBounds.getWidth(), screenBounds.getHeight());
        jogoScene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        stage.setScene(jogoScene);
        stage.setFullScreen(true);

        Label pontuacao = new Label("");
        pontuacao.getStyleClass().add("status");
        pontuacao.layoutXProperty().bind(jogoLayout.widthProperty().multiply(0)); // ajuste para seu layout
        pontuacao.layoutYProperty().bind(jogoLayout.heightProperty().multiply(0));
        jogoLayout.getChildren().add(pontuacao);

        Label status = new Label();
        status.getStyleClass().add("status");
        status.layoutXProperty().bind(jogoLayout.widthProperty().multiply(0)); // ajuste para seu layout
        status.layoutYProperty().bind(jogoLayout.heightProperty().multiply(0.15));
        jogoLayout.getChildren().add(status);

        Button novaRodada = new Button("Nova Rodada");
        novaRodada.getStyleClass().add("botao");
        novaRodada.layoutXProperty().bind(jogoLayout.widthProperty().multiply(0.33));
        novaRodada.layoutYProperty().bind(jogoLayout.heightProperty().multiply(1.08));
        jogoLayout.getChildren().add(novaRodada);

        Runnable atualizaponto = () -> {
            int pontosDealer = dealer.isTurnoDealer()
                    ? dealer.getPontos() // mostra total só se for a vez dele
                    : dealer.getPontuacaoParcial(); // mostra apenas a carta visível

            pontuacao.setText(
                    jogador1.getNome() + ": " + jogador1.getPontos() + " pontos\n" +
                            "Dealer: " + pontosDealer + " pontos\n"+"JogadorIA :"+ia.getPontos()+" pontos\n");
        };

        Runnable jogando = () -> {

            novaRodada.setDisable(true);

            // Começa a rodada com delay entre cada carta
            PauseTransition delay1 = new PauseTransition(Duration.seconds(0.5));
            PauseTransition delay2 = new PauseTransition(Duration.seconds(0.5));
            PauseTransition delay3 = new PauseTransition(Duration.seconds(0.5));
            PauseTransition delay4 = new PauseTransition(Duration.seconds(0.5));

            // 1ª carta do dealer
            delay1.setOnFinished(e -> {
                dealer.Hit(deck.dealCard());
                dealer.mostrarMao(dealerCartas);
                atualizaponto.run();
                delay2.play();
            });

            // 1ª carta do jogador
            delay2.setOnFinished(e -> {
                jogador1.Hit(deck.dealCard());
                jogador1.mostrarMao(jogadorCartas);

                ia.Hit(deck.dealCard());
                ia.mostrarMao(iaCartas);


                atualizaponto.run();
                delay3.play();
            });

            // 2ª carta do dealer (oculta)
            delay3.setOnFinished(e -> {
                dealer.Hit(deck.dealCard());
                dealer.maoDealerInicial(dealerCartas);
                atualizaponto.run();
                delay4.play();

            });

            // 2ª carta do jogador
            delay4.setOnFinished(e -> {
                jogador1.Hit(deck.dealCard());
                jogador1.mostrarMao(jogadorCartas);

                ia.Hit(deck.dealCard());
                ia.mostrarMao(iaCartas);

                atualizaponto.run();
            });

            // Inicia o encadeamento
            delay1.play();

            hit.setOnAction(e -> {
                jogador1.Hit(deck.dealCard());
                jogador1.mostrarMao(jogadorCartas);
                atualizaponto.run();

                boolean turnoFinalizado = false;

                if (jogador1.getPontos() == 21) {
                    status.setText(jogador1.getNome() + " fez Black Jack!");
                    turnoFinalizado = true;
                } else if (jogador1.getPontos() > 21) {
                    status.setText(jogador1.getNome() + " estourou! Pontos: " + jogador1.getPontos());
                    turnoFinalizado = true;
                }

                if (turnoFinalizado) {
                    hit.setDisable(true);
                    novaRodada.setDisable(true);
                    stand.fire();
                    stand.setDisable(true);
                }
            });

            Runnable turnoDealer = () ->{
                dealer.setTurnoDealer(true);
                dealer.mostrarMao(dealerCartas);
                atualizaponto.run();

                // Determinar alvo do dealer: maior ponto entre jogadores que não estouraram
                int pontoAlvo = 0;

                if (jogador1.getPontos() <= 21) pontoAlvo = jogador1.getPontos();
                if (ia.getPontos() <= 21 && ia.getPontos() > pontoAlvo) pontoAlvo = ia.getPontos();

                // Se todos estouraram, dealer não precisa jogar
                if (pontoAlvo == 0) {
                    status.setText("Todos os jogadores estouraram! Dealer vence automaticamente.");
                } else {
                    // Dealer joga enquanto estiver abaixo do alvo e abaixo de 21
                    while (dealer.getPontos() < pontoAlvo && dealer.getPontos() < 21) {
                        dealer.Hit(deck.dealCard());
                        dealer.mostrarMao(dealerCartas);
                        atualizaponto.run();
                    }

                    // Determinar resultado
                    if (dealer.getPontos() > 21) {
                        status.setText("Dealer estourou! Jogadores que não estouraram vencem.");
                    } else if (dealer.getPontos() == pontoAlvo) {
                        status.setText("Empate com o maior jogador! Dealer: " + dealer.getPontos());
                    } else if (dealer.getPontos() > pontoAlvo) {
                        status.setText("Dealer venceu! Dealer: " + dealer.getPontos());
                    } else {
                        status.setText("Dealer parou abaixo do alvo. Dealer: " + dealer.getPontos());
                    }
                }

                // Atualiza interface
                hit.setDisable(true);
                stand.setDisable(true);
                novaRodada.setDisable(false);
                dealer.setTurnoDealer(false);

            };

            stand.setOnAction(e -> {
                hit.setDisable(true);
                stand.setDisable(true);
                atualizaponto.run();

                int maxJogadasIA = 5;

                new Thread(() -> {
                    int jogadas = 0;

                    while (jogadas < maxJogadasIA && ia.getPontos() < 21) {

                        // Chama a IA para decidir a jogada
                        String iaResposta = ia.sendIA(jogador1.getMao(), dealer.cartadealer())
                                .trim()
                                .toLowerCase();



                        // Atualiza a GUI
                        Platform.runLater(() -> {
                            if (iaResposta.equals("h")) {
                                ia.Hit(deck.dealCard());
                                ia.mostrarMao(iaCartas);
                                atualizaponto.run();
                            }
                        });

                        jogadas++;

                        // Pequena pausa para não sobrecarregar a IA
                        try { Thread.sleep(1000); } catch (InterruptedException ex) { ex.printStackTrace(); }

                        if (iaResposta.equals("s")) break; // IA decidiu parar
                    }

                    // Quando a IA termina, atualiza a GUI e chama o dealer
                    Platform.runLater(() -> {
                        novaRodada.setDisable(false);
                        status.setText("Turno da IA finalizado. Pontos IA: " + ia.getPontos());
                        turnoDealer.run();
                    });

                }).start();
            });




        };

        novaRodada.setOnAction(e -> {

            deck = new DeckOfCards();
            deck.shuffle();
            jogador1.resetMao();
            ia.resetMao();
            dealer.resetMao();
            dealer.setTurnoDealer(false);
            status.setText("");
            hit.setDisable(false);
            stand.setDisable(false);
            dealerCartas.getChildren().clear();
            jogadorCartas.getChildren().clear();
            iaCartas.getChildren().clear();
            atualizaponto.run();
            jogando.run();
        });

        stage.show();
        jogando.run();
    }
}
