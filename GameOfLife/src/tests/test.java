package tests;

import automaton.*;
import cells.*;
import visitors.Visitor;
import visitors.VisitorClassic;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Testing class. Contains main method that launches the execution of the game.
 */
public class test {
    public static void main(String[] args){

        JeuDeLaVie x=new JeuDeLaVie();
        x.initializeGrid();
        JeuDeLaVieUI ui = new JeuDeLaVieUI(x);
    }
}
