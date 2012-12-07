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
import wjd.teutoburg.Palette;

/**
 *
 * @author wdyce
 * @since Dec 7, 2012
 */
public class Faction 
{
  /* ATTRIBUTES */
  public final Colour colour_shield, colour_tunic, colour_face, colour_weapon, 
                      colour_imposter;
  
  /* METHODS */
  private Faction(Colour shield, Colour body, Colour head, Colour weapon)
  {
    colour_shield = shield;
    colour_tunic = body;
    colour_face = head;
    colour_weapon = weapon;
    colour_imposter = body.clone().avg(shield);
  }
  
  /* IMPLEMENTATIONS */
  
  public static final Faction ROMAN = new Faction(
      Palette.ROMAN_SHIELD, Palette.ROMAN_BODY, Palette.ROMAN_HEAD, 
      Palette.ROMAN_WEAPON);
  
  public static final Faction BARBARIAN = new Faction(
      Palette.BARBARIAN_SHIELD, Palette.BARBARIAN_BODY, Palette.BARBARIAN_HEAD,
      Palette.ROMAN_WEAPON);
}
