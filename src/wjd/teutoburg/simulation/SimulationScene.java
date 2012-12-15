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
import wjd.amb.view.Colour;
import wjd.amb.view.ICamera;
import wjd.amb.view.ICanvas;
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
	// romans
	private static final float ROMAN_DEPLOY_FRAC = 0.1f;
	private static final int ROMAN_N_REGIMENTS = 15;
	// barbarians
	private static final float BARB_DEPLOY_FRAC = 0.45f;
	private static final int BARBARIAN_N_REGIMENTS = 20;

	/* ATTRIBUTES */
	private Rect map;
	private Rect roman_deploy;
	private Rect barb_deploy_E, barb_deploy_W;
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
		roman_deploy = new Rect(size.x * (1.0f - ROMAN_DEPLOY_FRAC) * 0.5f, 
				size.y * (1.0f - ROMAN_DEPLOY_FRAC), 
				size.x * ROMAN_DEPLOY_FRAC, 
				size.y * ROMAN_DEPLOY_FRAC);


		barb_deploy_W = new Rect(0, 0, size.x * BARB_DEPLOY_FRAC - 1, size.y);
		barb_deploy_E = new Rect(size.x * (1.0f - BARB_DEPLOY_FRAC), 
				0,
				size.x * BARB_DEPLOY_FRAC, 
				size.y);

		// collisions and percepts
		grid = new TileGrid(size.clone().scale(Tile.ISIZE).ceil());
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
		/*Rect corridor = roman_deploy.clone();
    corridor.y = 0;
    corridor.h = map.h;*/

		for(int c = 0; c < copse_n; c++)
		{
			Copse copse = new Copse(V2.ORIGIN);
			V2 p = copse.getCircle().centre;
			/*do
      {*/
			((c%2 == 0) ? barb_deploy_W : barb_deploy_E).randomPoint(p);
			//} while(copse.getCircle().collides(corridor));

			// add the finished copse to the list
			grid.registerCopse(copse);
			copses.add(copse);
		}
	}

	private void deployRomans()
	{
		V2 target = roman_deploy.getCentre();
		target.y = 0;
		for(int i = 0; i < ROMAN_N_REGIMENTS; i++)
		{
			V2 p = new V2();
			roman_deploy.randomPoint(p);
			RegimentAgent r = Faction.ROMAN.createRegiment(p, grid.pixelToTile(p));
			r.faceTowards(target);
			agents.add(r);
		}
	}

	private void deployBarbarians()
	{
		/*V2 target = roman_deploy.getCentre();
		for(int i = 0; i < BARBARIAN_N_REGIMENTS; i++)
		{
			V2 p = new V2();
			((i%2 == 0) ? barb_deploy_W : barb_deploy_E).randomPoint(p);
			RegimentAgent r = Faction.BARBARIAN.createRegiment(p, grid.pixelToTile(p));
			r.faceTowards(target);
			agents.add(r);
		}*/
		V2 p;
		Tile tilep;
		List<Tile> neighbours;
		V2 dir_target = new V2(0,0), tmp = new V2(0,0);
		for(int i = 0; i < BARBARIAN_N_REGIMENTS; i++)
		{
			p = new V2(((i%2 == 0) ? barb_deploy_W.endx() : barb_deploy_E.x), map.w/2);
			tilep = grid.pixelToTile(p);
			while(tilep.forest_amount.balance() < 0.3 || tilep.agent != null)
			{
				neighbours = grid.getNeighbours(tilep, true);
				dir_target.xy(0,0);
				for(Tile t : neighbours)
				{
					if(t.agent != null || t.forest_amount.balance() < 0.3)
					{
						tmp.reset(p).sub(t.pixel_position);
						tmp.normalise();
						dir_target.add(tmp);
					}
				}
				dir_target.normalise();
				dir_target.add(((i%2 == 0) ? -1 : 1),0);
				dir_target.normalise();
				dir_target.scale(1);
				p.add(dir_target);
				
				tilep = grid.pixelToTile(p);
			}
			RegimentAgent r = Faction.BARBARIAN.createRegiment(p, grid.pixelToTile(p));
			r.faceTowards(roman_deploy.getCentre());
			agents.add(r);
		}
	}

	/* IMPLEMENTS -- IDYNAMIC */

	@Override
	public EUpdateResult update(int t_delta)
	{
		// update all the agents
		for(Agent a : agents)
    {
			if(a.update(t_delta) == EUpdateResult.DELETE_ME)
        agents.remove(a);
    }

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


		canvas.setColour(Colour.RED);
		canvas.box(roman_deploy, false);
		canvas.setColour(Colour.BLUE);
		canvas.box(barb_deploy_E, false);
		canvas.box(barb_deploy_W, false);

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
