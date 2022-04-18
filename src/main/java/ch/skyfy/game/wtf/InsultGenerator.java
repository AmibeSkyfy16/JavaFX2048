package ch.skyfy.game.wtf;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("SpellCheckingInspection")
public class InsultGenerator {
    public static void main(String[] args) {
        Random random = ThreadLocalRandom.current();

        String[] insult1 = new String[]{"Salut", "Yo", "Hola", "Hello", "Hey"};
        String[] insult2 = new String[]{"grosse merde", "vieille truie", "vieux batard", "gros chien", "sale pétasse"};
        String insult3 = "tu ressembles à";
        String[] insult4 = new String[]{"une sous-merde", "un gland puant", "une bouze de vache","mes poils de cul"};
        String[] insult5 = new String[]{"Va te faire mettre", "Va te faire trouer le cul", "Va te faire bourrer le cul chez les grecs","Va crever","Va te faire sucer"};
        String signe = "Signé : ";
        String[] insult6 = new String[]{"ton pote", "ton pire cauchemar", "ton vieux père alcoolique","ton pire ennemi"};


        String insultTotal = "";

        insultTotal += insult1[random.nextInt(insult1.length)] + " ";
        insultTotal += insult2[random.nextInt(insult2.length)] + " ";
        insultTotal += insult3 + " ";
        insultTotal += insult4[random.nextInt(insult4.length)] + " ";
        insultTotal += insult5[random.nextInt(insult5.length)] + " ";
        insultTotal += signe + " ";
        insultTotal += insult6[random.nextInt(insult6.length)] + " ";

        System.out.println(insultTotal);
    }
}
