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
package wjd.teutoburg.agent;

import wjd.amb.view.Colour;
import wjd.amb.view.ICanvas;
import wjd.math.Rect;
import wjd.math.V2;

/**
 *
 * @author wdyce
 * @since Dec 4, 2012
 */
public abstract class Soldier
{
  /* INTERFACE */
  
  public abstract void render(ICanvas canvas, V2 position, V2 direction);
  
  /* IMPLEMENTATIONS */
  public static final Soldier ROMAN = new Soldier()
  {
    // attributes
    //private Rect body = new Rect(0.0f, 0.0f, 3.0f, 6.0f);
    //private Rect shield = new Rect(0.0f, 0.0f, 4.0f, 5.0f);
    private Rect body = new Rect(0.0f, 0.0f, 6.0f, 12.0f);
    private Rect shield = new Rect(0.0f, 0.0f, 8.0f, 10.0f);
    
    // implements -- soldier
    @Override
    public void render(ICanvas canvas, V2 position, V2 direction)
    {
      // position body and shield
      body.centrePos(position);
      shield.centrePos(position).shift(direction.left().scale(2));
      direction.right();
      
      shield.w = 1+3*Math.abs(direction.y);
      
      canvas.setColour(Colour.BLACK);
        canvas.circle(position.add(0, 7), 5, true);
      position.add(0, -14);
      
      // up => shield is further than body
      if(direction.y < 0)
      {
        canvas.setColour(Colour.VIOLET);
          canvas.box(shield, true);
        canvas.setColour(Colour.YELLOW);
          canvas.circle(position, 2, true);
        canvas.setColour(Colour.RED);
          canvas.box(body, true);

      }
      // down => body is further than shield
      else
      {
        canvas.setColour(Colour.RED);
          canvas.box(body, true);
        canvas.setColour(Colour.YELLOW);
          canvas.circle(position, 2, true);
        canvas.setColour(Colour.VIOLET);
          canvas.box(shield, true);
      }
    }
  };
}
