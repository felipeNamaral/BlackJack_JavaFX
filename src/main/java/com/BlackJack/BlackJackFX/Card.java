package com.BlackJack.BlackJackFX;


public class Card
{
    private final String face; // face of card ("Ace", "Deuce", ...)
    private final String suit; // suit of card ("Hearts", "Diamonds", ...)

    // two-argument constructor initializes card's face and suit
    public Card(String face, String suit)
    {
        this.face = face;
        this.suit = suit;
    }

    @Override
    public String toString()
    {
        return face + " of " + suit;
    }

    public String getFace() {
        return face;
    }
    public String getSuit() {

        return suit;}

    public String getImagePath() {
        String faceLower = face.toLowerCase(); // ex: "Ace" -> "ace"
        String suitLower = suit.toLowerCase(); // ex: "Clubs" -> "clubs"
        return "/images/cartas/" + faceLower + "_of_" + suitLower + ".png";
    }




} // end class Card