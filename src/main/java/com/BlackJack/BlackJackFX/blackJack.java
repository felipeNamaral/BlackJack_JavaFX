package com.BlackJack.BlackJackFX;

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

public class blackJack {

    private DeckOfCards deck;
    private Dealer dealer;
    private Jogador jogador1;
    // private Jogador jogador2;

    public blackJack(DeckOfCards deck, Dealer dealer, Jogador jogador1) {
        this.deck = deck;
        this.dealer = dealer;
        this.jogador1 = jogador1;
        this.deck.shuffle();
    }

    public blackJack(DeckOfCards deck, Dealer dealer, Jogador jogador1, Jogador jogador2) {
        this.deck = deck;
        this.dealer = dealer;
        this.jogador1 = jogador1;
        // this.jogador2 = jogador2;
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

        // Botões Hit/Stand
        Button hit = new Button("Hit");
        Button stand = new Button("Stand");
        hit.getStyleClass().add("botao");
        stand.getStyleClass().add("botao");

        HBox botoes = new HBox(20, hit, stand);
        botoes.layoutXProperty().bind(jogoLayout.widthProperty().multiply(0.7));
        botoes.layoutYProperty().bind(jogoLayout.heightProperty().multiply(1.08));

        jogoLayout.getChildren().addAll(dealerCartas, jogadorCartas, botoes);

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
        status.layoutYProperty().bind(jogoLayout.heightProperty().multiply(0.1));
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
                            "Dealer: " + pontosDealer + " pontos");
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

                atualizaponto.run();
            });

            // Inicia o encadeamento
            delay1.play();

            hit.setOnAction(e -> {
                jogador1.Hit(deck.dealCard());
                jogador1.mostrarMao(jogadorCartas);
                atualizaponto.run();
                if (jogador1.getPontos() == 21) {
                    status.setText(jogador1.getNome() + " fez Black Jack!");
                    hit.setDisable(true);
                    dealer.mostrarMao(dealerCartas);
                    stand.setDisable(true);
                    novaRodada.setDisable(false);
                }
                if (jogador1.getPontos() > 21) {
                    status.setText(jogador1.getNome() + " estourou! Pontos: " + jogador1.getPontos());
                    dealer.mostrarMao(dealerCartas);
                    hit.setDisable(true);
                    dealer.setTurnoDealer(true);
                    dealer.mostrarMao(dealerCartas);
                    atualizaponto.run();
                    stand.setDisable(true);
                    novaRodada.setDisable(false);
                } else if (jogador1.getPontos() == 21) {
                    status.setText(jogador1.getNome() + " fez Black Jack!");
                    hit.setDisable(true);
                    stand.setDisable(true);
                    novaRodada.setDisable(false);
                }
            });

            stand.setOnAction(e -> {
                dealer.setTurnoDealer(true);
                dealer.mostrarMao(dealerCartas);
                atualizaponto.run();

                dealer.mostrarMao(dealerCartas);
                if (jogador1.getPontos() != 21) {
                    while (dealer.getPontos() < jogador1.getPontos()) {
                        dealer.Hit(deck.dealCard());
                        dealer.mostrarMao(dealerCartas);
                        atualizaponto.run();
                    }
                }
                status.setText("Turno do Dealer finalizado. Dealer: " + dealer.getPontos() + " pontos.");
                hit.setDisable(true);
                stand.setDisable(true);
                novaRodada.setDisable(false);
            });

        };

        novaRodada.setOnAction(e -> {

            deck = new DeckOfCards();
            deck.shuffle();
            jogador1.resetMao();
            dealer.resetMao();
            dealer.setTurnoDealer(false);
            status.setText("");
            hit.setDisable(false);
            stand.setDisable(false);
            dealerCartas.getChildren().clear();
            jogadorCartas.getChildren().clear();
            atualizaponto.run();
            jogando.run();
        });

        stage.show();
        jogando.run();
    }
}
