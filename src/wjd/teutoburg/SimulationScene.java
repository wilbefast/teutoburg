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

import wjd.teutoburg.agent.BarbarianRegiment;
import java.util.LinkedList;
import java.util.List;
import wjd.amb.AScene;
import wjd.amb.control.EUpdateResult;
import wjd.amb.control.IInput;
import wjd.amb.view.Colour;
import wjd.amb.view.ICamera;
import wjd.amb.view.ICanvas;
import wjd.math.V2;
import wjd.teutoburg.agent.Agent;
import wjd.teutoburg.agent.RomanRegiment;

/**
 * @author wdyce
 * @since 05-Oct-2012
 */
public class SimulationScene extends AScene
{
  /* CONSTANTS */
  public static final V2 GRIDSIZE = new V2(64, 64);
  
  /* ATTRIBUTES */
  private StrategyCamera camera;
  private List<Agent> agents;

  /* METHODS */
  
  // constructors
  public SimulationScene()
  {
    // view
    camera = new StrategyCamera(null); // FIXME add boundary
    agents = new LinkedList<Agent>();
    
    for(int i = 0; i < 3; i++)
      agents.add(new RomanRegiment(new V2((float)Math.random()*1400, (float)Math.random()*1400)));
    
    for(int i = 0; i < 3; i++)
      agents.add(new BarbarianRegiment(new V2((float)Math.random()*1400, (float)Math.random()*1400)));
      
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
    
    // draw all the agents
    canvas.setCamera(camera);
    canvas.setColour(Colour.VIOLET);
    for(Agent a : agents)
      a.render(canvas);
      
    // render GUI elements
    canvas.setCameraActive(false);
    //! TODO
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
