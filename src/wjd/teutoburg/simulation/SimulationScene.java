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
import wjd.amb.view.ICamera;
import wjd.amb.view.ICanvas;
import wjd.math.Rect;
import wjd.math.V2;
import wjd.teutoburg.MenuScene;
import wjd.teutoburg.physics.Agent;
import wjd.teutoburg.physics.Tree;
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
  // forest
  private static final int COPSE_BASE_N = 2;
  private static final int COPSE_N_TREES = 30;
  private static final float COPSE_SIZE = Tree.COLLISION_RADIUS*COPSE_N_TREES*4;
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
  private List<Agent> agents;
  private List<Tree> trees;

  /* METHODS */
  
  // constructors
  public SimulationScene(V2 size)
  {
    // boundaries
    map = new Rect(V2.ORIGIN, size);
    roman_deployment = map.clone().scale(ROMAN_DEPLOYMENT_FRACTION);
    barbarian_illegal_deployment = map.clone().scale(1 - BARBARIAN_DEPLOYMENT_FRACTION);
    
    // generate forest
    trees = new LinkedList<Tree>();
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
    float copse_n = COPSE_BASE_N * (map.w * map.h) / (COPSE_SIZE * COPSE_SIZE);
    
    
    Rect copse = new Rect(0, 0, COPSE_SIZE, COPSE_SIZE);
    for(int c = 0; c < copse_n; c++)
    {
      int attempts = 0;
      do
      {
        copse.xy((float)(Math.random()*(map.w - copse.w)), 
                 (float)(Math.random()*(map.h - copse.h)));
        attempts++;
      }
      while(copse.collides(roman_deployment) && attempts < 10);
      
      for(int t = 0; t < COPSE_N_TREES; t++)
      {
        V2 p = new V2();
        copse.randomPoint(p);
        trees.add(new Tree(p));
      }
    }
  }
  
  private void deployRomans()
  {
    for(int i = 0; i < ROMAN_N_REGIMENTS; i++)
    {
      V2 p = new V2();
      roman_deployment.randomPoint(p);
      RegimentAgent r = Faction.ROMAN.createRegiment(p);
      r.faceRandom();
      agents.add(r);
    }
  }
  
  private void deployBarbarians()
  {
    V2 centre = map.getCentre();
    for(int i = 0; i < BARBARIAN_N_REGIMENTS; i++)
    {
      V2 p = new V2();
      int attempts = 0;
      do
      {
        map.randomPoint(p);
        attempts++;
      }
      while(barbarian_illegal_deployment.contains(p) && attempts < GENERATOR_MAX_ATTEMPTS);
      RegimentAgent r = Faction.BARBARIAN.createRegiment(p);
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
    for(Tree t : trees)
      t.render(canvas);

    // draw all the agents
    for(Agent a : agents)
      a.render(canvas);
      
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
