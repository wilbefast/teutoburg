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
package wjd.teutoburg.simulation;

import java.util.LinkedList;
import java.util.List;
import wjd.amb.AScene;
import wjd.amb.control.EUpdateResult;
import wjd.amb.control.IInput;
import wjd.amb.rts.StrategyCamera;
import wjd.amb.view.ICamera;
import wjd.amb.view.ICanvas;
import wjd.math.Circle;
import wjd.math.Rect;
import wjd.math.V2;
import wjd.teutoburg.MenuScene;
import wjd.teutoburg.collision.Agent;
import wjd.teutoburg.forest.Copse;
import wjd.teutoburg.regiment.Faction;
import wjd.teutoburg.regiment.RegimentAgent;

/**
 * @author wdyce
 * @since 05-Oct-2012
 */
public class SimulationScene extends AScene
{
  /* CONSTANTS */
  private static final int GENERATOR_MAX_ATTEMPTS = 20;
  // romans
  private static final float ROMAN_DEPLOYMENT_FRACTION = 0.2f;
  private static final int ROMAN_N_REGIMENTS = 15;
  // barbarians
  private static final float BARBARIAN_DEPLOYMENT_FRACTION = 0.3f;
  private static final int BARBARIAN_N_REGIMENTS = 20;
  
  /* ATTRIBUTES */
  private Rect map;
  private Rect roman_deployment;
  private Rect barbarian_illegal_deployment;
  private StrategyCamera camera;
  private TileGrid grid;
  private List<Agent> agents;
  private List<Copse> copses;

  /* METHODS */
  
  // constructors
  public SimulationScene(V2 size)
  {
    // boundaries
    map = new Rect(V2.ORIGIN, size);
    roman_deployment = map.clone().scale(ROMAN_DEPLOYMENT_FRACTION);
    barbarian_illegal_deployment = map.clone().scale(1 - BARBARIAN_DEPLOYMENT_FRACTION);
    
    // collisions and percepts
    grid = new TileGrid(size.clone().scale(Tile.ISIZE));
    grid.clear();
    
    // generate forest
    copses = new LinkedList<Copse>();
    generateForest();
    
    // deploy soldiers
    agents = new LinkedList<Agent>();
    deployRomans();
    deployBarbarians();
    
    // view
    camera = new StrategyCamera(map);
  }

  // mutators
  
  // accessors
  
  public ICamera getCamera() { return camera; }
  
  /* SUBROUTINES */
  
  private void generateForest()
  {
    float copse_n = Copse.NUMBER_FACTOR * (map.w * map.h) / (Copse.SIZE * Copse.SIZE);
    
    for(int c = 0; c < copse_n; c++)
    {
      Copse copse = new Copse(V2.ORIGIN);
      Circle copse_c = copse.getCircle();
      int attempts = 0;
      do
      {
        copse_c.setCentre(copse_c.radius + 
                        (float)(Math.random()*(map.w - 2*copse_c.radius)), 
                              copse_c.radius + 
                        (float)(Math.random()*(map.h - 2*copse_c.radius)));
        attempts++;
      }
      while(copse_c.collides(roman_deployment) && attempts < 10);
          
      // add the finished copse to the list
      grid.registerCopse(copse);
      copses.add(copse);
    }
  }
  
  private void deployRomans()
  {
    for(int i = 0; i < ROMAN_N_REGIMENTS; i++)
    {
      V2 p = new V2();
      roman_deployment.randomPoint(p);
      RegimentAgent r = Faction.ROMAN.createRegiment(p, grid.pixelToTile(p));
      r.faceRandom();
      agents.add(r);
    }
  }
  
  private void deployBarbarians()
  {
    V2 centre = map.getCentre();
    for(int i = 0; i < BARBARIAN_N_REGIMENTS; i++)
    {
      V2 p = new V2(), grid_p = new V2();
      int attempts = 0;
      do
      {
        map.randomPoint(p);
        attempts++;
      }
      while(barbarian_illegal_deployment.contains(p) && attempts < GENERATOR_MAX_ATTEMPTS);
      grid_p = p.clone().scale(Tile.ISIZE);
      RegimentAgent r = Faction.BARBARIAN.createRegiment(p, grid.pixelToTile(p));
      r.faceTowards(centre);
      agents.add(r);
    }
  }
  
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
    canvas.setColour(Palette.GRASS);
    canvas.box(map, true);
    
    // draw all the trees
    for(Copse c : copses)
      c.render(canvas);

    // draw all the agents
    for(Agent a : agents)
      a.render(canvas);
    
    for(Tile t : grid)
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
