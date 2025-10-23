package com.BlackJack.BlackJackFX;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class Jogador {
    private int pontos = 0;
    private String nome;
    private List<Card> mao = new ArrayList<>();
    private int aces =0;
    public Jogador(String nome) {
        this.nome = nome;
    }

    public void adicionarCarta(Card carta) {
        mao.add(carta);
        if (carta.getFace().equals("Ace")) {
            pontos += 11;
            aces++;
        } else if (carta.getFace().equals("2")) {
            pontos += 2;
        } else if (carta.getFace().equals("3")) {
            pontos += 3;
        } else if (carta.getFace().equals("4")) {
            pontos += 4;
        } else if (carta.getFace().equals("5")) {
            pontos += 5;
        } else if (carta.getFace().equals("6")) {
            pontos += 6;
        } else if (carta.getFace().equals("7")) {
            pontos += 7;
        } else if (carta.getFace().equals("8")) {
            pontos += 8;
        } else if (carta.getFace().equals("9")) {
            pontos += 9;
        } else {
            pontos += 10;
        }

        while (pontos > 21 && aces > 0) {
            pontos -= 10;
            aces--;
        }
    }


    public void mostrarMao() {
        System.out.println(nome + " tem as cartas:");
        for (Card c : mao) {
            System.out.println("  " + c);
        }
        System.out.println("Total de pontos: " + pontos);
    }


    public void mostrarMao(HBox container) {

        container.getChildren().clear(); // limpa cartas antigas
        for (Card c : mao) {
            ImageView cartaView = new ImageView(new Image(
                    getClass().getResource("/images/cartas/" + c.getFace().toLowerCase() + "_of_" + c.getSuit().toLowerCase() + ".png")
                            .toExternalForm()
            ));
            cartaView.setFitWidth(80);
            cartaView.setFitHeight(120);
            container.getChildren().add(cartaView);}
    }




    public void Hit(Card carta) {
        adicionarCarta(carta);
    }

    public void resetMao() {
        mao.clear();
        pontos = 0;
    }

    public void fistHand(Card carta1, Card carta2) {
        adicionarCarta(carta1);
        adicionarCarta(carta2);
    }

    protected List<Card> getMao() {
        return mao;
    }
    public int getPontos() {
        return pontos;
    }
    public String getNome() {
        return nome;
    }
}
