/*
 Copyright (C) 2012 William James Dyce

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package wjd.teutoburg;

import java.util.LinkedList;
import java.util.List;
import wjd.amb.AScene;
import wjd.amb.control.EUpdateResult;
import wjd.amb.control.IInput;
import wjd.amb.view.Colour;
import wjd.amb.view.ICamera;
import wjd.amb.view.ICanvas;
import wjd.math.Rect;
import wjd.math.V2;
import wjd.teutoburg.agent.Agent;
import wjd.teutoburg.agent.BarbarianRegiment;
import wjd.teutoburg.agent.RomanRegiment;
import wjd.teutoburg.agent.Tree;

/**
 * @author wdyce
 * @since 05-Oct-2012
 */
public class SimulationScene extends AScene
{
  /* CONSTANTS */
  private static final Colour C_GRASS = new Colour(120, 255, 33);
  
  /* ATTRIBUTES */
  private Rect area;
  private StrategyCamera camera;
  private List<Agent> agents;
  private List<Tree> trees;

  /* METHODS */
  
  // constructors
  public SimulationScene(V2 size)
  {
    
    // model
    area = new Rect(V2.ORIGIN, size);
    
    agents = new LinkedList<Agent>();
    V2 p = new V2();
    for(int i = 0; i < 10; i++)
    {
      area.randomPoint(p);
      agents.add(new RomanRegiment(p.clone()));
      area.randomPoint(p);
      agents.add(new BarbarianRegiment(p.clone()));
    }
    
    trees = new LinkedList<Tree>();
    Rect copse = new Rect(0, 0, area.w/4, area.h/4);
    for(int i = 0; i < 20; i++)
    {
      copse.xy((float)(Math.random()*(area.w - copse.w)), 
               (float)(Math.random()*(area.h - copse.h)));
      for(int j = 0; j < 40; j++)
      {
        copse.randomPoint(p);
        trees.add(new Tree(p.clone()));
      }
    }
    
    // view
    camera = new StrategyCamera(area);
  }

  // mutators
  
  // accessors
  
  public ICamera getCamera() { return camera; }
  
  /* IMPLEMENTS -- IDYNAMIC */

  @Override
  public EUpdateResult update(int t_delta)
  {
    // update all the agents
    for(Agent a : agents)
      a.update(t_delta);
    
    // all clear!
    return EUpdateResult.CONTINUE;
  }

  /* IMPLEMENTS -- IVISIBLE */
  
  @Override
  public void render(ICanvas canvas)
  {
    // clear the screen
    canvas.clear();
    
    canvas.setCamera(camera);
    
    // draw the grass
    canvas.setColour(C_GRASS);
    canvas.box(area, true);
    
    // draw all the agents
    for(Agent a : agents)
      a.render(canvas);
    for(Tree t : trees)
      t.render(canvas);
      
    // render GUI elements
    canvas.setCameraActive(false);
  }
  
  /* OVERRIDES -- CONTROLLER */
  
  @Override
  public EUpdateResult processInput(IInput input)
  {
    // pass up
    EUpdateResult result = super.processInput(input);
    if(result != EUpdateResult.CONTINUE)
      return result;
    
    /*if(input.isKeyHeld(IInput.EKeyCode.L_CTRL))
      agents.get(0).turn(-0.1f);
    
    if(input.isKeyHeld(IInput.EKeyCode.L_ALT))
      agents.get(0).turn(0.1f);*/
    
    // control camera
    camera.processInput(input);
    
    // all clear
    return EUpdateResult.CONTINUE;
  }
  
  /* IMPLEMENTS -- ICONTROLLER */
  @Override
  public EUpdateResult processKeyPress(IInput.KeyPress event)
  {    
    if(event.pressed)
    {
      if(event.key != null) switch(event.key)
      {
        case ESC:
          setNext(new MenuScene());
          return EUpdateResult.REPLACE_ME;
      }
    }

    // all clear
    return EUpdateResult.CONTINUE;
  }
}
