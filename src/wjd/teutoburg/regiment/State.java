/*
 Copyright (C) 2012 William James Dyce, Chloé Desdouits

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
package wjd.teutoburg.regiment;

/**
 *
 * @author wdyce
 * @since Dec 18, 2012
 */
public class State implements Comparable<State>
{
  /* CONSTANTS */
  static final State
    WAITING = new State(1, "waiting"),
    CHARGING = new State(2, "charging"),
    FIGHTING = new State(3, "fighting"),
    DEAD = new State(4, "dead");
  
  /* ATTRIBUTES */
  final int value;
  final String key;

  /* METHODS */
  public State(int v, String k) 
  {
    value = v; 
    key = k;
  }		  


  @Override
  public int compareTo(State other)
  {
    return value - other.value;
  }

  @Override
  public String toString()
  {
    return key;
  }
}
