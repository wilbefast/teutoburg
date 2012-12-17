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

import wjd.amb.view.Colour;

/**
 * Colours used in the simulation are gathered here.
 *
 * @author wdyce
 * @since Dec 7, 2012
 */
public class Palette 
{
  /* CONSTANTS */
  // grass
  public static final Colour GRASS = new Colour(162, 242, 93);
  public static final Colour GRASS_SHADOW = GRASS.clone().mult(0.3f);
  // trees
  public static final Colour TREE_TRUNK = new Colour(100, 200, 50);
  public static final Colour TREE_LEAVES = new Colour(50, 150, 100);
  public static final Colour TREE_IMPOSTER 
            = GRASS_SHADOW.clone().avg(TREE_TRUNK).avg(TREE_LEAVES);
  public static final Colour COPSE_IMPOSTER
            = TREE_IMPOSTER.clone().avg(GRASS);
  // romans
  public static final Colour ROMAN_BODY = new Colour(255, 255, 0);
  public static final Colour ROMAN_HEAD = new Colour(255, 225, 225);
  public static final Colour ROMAN_SHIELD = new Colour(255, 0, 0);
  public static final Colour ROMAN_WEAPON = new Colour(100, 50, 25);
  // barbarians
  public static final Colour BARBARIAN_BODY = new Colour(0, 255, 255);
  public static final Colour BARBARIAN_HEAD = new Colour(225, 225, 255);
  public static final Colour BARBARIAN_SHIELD = new Colour(0, 0, 255);
  public static final Colour BARBARIAN_WEAPON = new Colour(25, 50, 100);
  // combat
  public static final Colour BLOOD = GRASS.clone().avg(Colour.RED);
  
}
