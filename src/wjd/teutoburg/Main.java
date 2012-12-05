package wjd.teutoburg;

import wjd.amb.AmbitionEngine;
import wjd.math.V2;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author william
 * @since 04-Dec-2012
 */
public class Main
{

  public static void main(String[] args)
  {

    AmbitionEngine.launch("Teutoburg", new V2(640, 480), new MenuScene(), null);
  }
}
