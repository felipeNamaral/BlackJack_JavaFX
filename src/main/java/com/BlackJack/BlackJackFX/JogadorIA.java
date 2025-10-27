package com.BlackJack.BlackJackFX;


import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

import java.util.List;
import java.util.stream.Collectors;


public class JogadorIA extends Jogador{


    Client client = Client.builder().apiKey(System.getenv("GEMINI_API_KEY")).build();

    public JogadorIA()
    {
        super("IA");
    }

    public String sendIA(List<Card> jogadormao, Card cartaDealer){
        System.out.println("vez da ia:");
        try {
            String maoFormatada = jogadormao.stream()
                    .map(Card::toString)
                    .collect(Collectors.joining(", "));

            String gamestatus = "Você está jogando BlackJack.\n" +
                    "Sua mão atual (IA): " + super.getMao() + "\n" +
                    "Carta visível do dealer: " + cartaDealer + "\n" +
                    "Cartas do jogador anterior: " + maoFormatada + "\n" +
                    "O que você quer fazer? Retorne apenas 's' para stand ou 'h' para hit.";

            GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.5-flash",
                    gamestatus,
                    null
            );
            System.out.println("IA respondeu: " + response.text());
            return response.text().trim().toLowerCase();

        } catch (com.google.genai.errors.ServerException se) {
            System.err.println("Erro de servidor: " + se.getMessage());
            // Retorna ação padrão caso a IA não esteja disponível
            return "s"; // por exemplo, stand
        } catch (Exception e) {
            e.printStackTrace();
            return "s"; // fallback
        }
    }
}
