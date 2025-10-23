package com.BlackJack.BlackJackFX;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class Dealer extends Jogador {

    boolean turnoDealer = false;

    public Dealer() {
        super("Dealer");
    }



    public void maoDealerInicial() {
        System.out.println("Dealer mostra: " + getMao().get(0));
        System.out.println("Segunda carta está oculta");
    }

    public boolean isTurnoDealer() {
        return turnoDealer;
    }



    public void maoDealerInicial(HBox container) {
        container.getChildren().clear();


        Card primeiraCarta = getMao().get(0);
        ImageView primeiraView = new ImageView(new Image(
                getClass().getResource("/images/cartas/" +
                        primeiraCarta.getFace().toLowerCase() + "_of_" +
                        primeiraCarta.getSuit().toLowerCase() + ".png").toExternalForm()
        ));
        primeiraView.setFitWidth(80);
        primeiraView.setFitHeight(120);
        container.getChildren().add(primeiraView);

        // Segunda carta oculta
        ImageView verso = new ImageView(new Image(
                getClass().getResource("/images/cartas/back.png").toExternalForm()
        ));
        verso.setFitWidth(80);
        verso.setFitHeight(120);
        container.getChildren().add(verso);
    }

    public void setTurnoDealer(boolean valor) {
        turnoDealer = valor;
    }

    public  Card cartadealer()
    {
        return getMao().getFirst();
    }


    public int getPontuacaoParcial() {
        if (getMao().isEmpty()) return 0;

        Card primeiraCarta = getMao().get(0); // só a carta visível
        int valor = 0;

        // agora precisamos converter a face em valor numérico
        switch (primeiraCarta.getFace()) {
            case "Ace":
                valor = 11; // ou 1, mas tanto faz por enquanto
                break;
            case "King":
            case "Queen":
            case "Jack":
                valor = 10;
                break;
            default:
                valor = Integer.parseInt(primeiraCarta.getFace()); // cartas 2–10
                break;
        }

        return valor;
    }

}
