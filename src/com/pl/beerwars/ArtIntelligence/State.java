package com.pl.beerwars.ArtIntelligence;
import java.util.*;

public abstract class State
{
	public abstract String serialize();
	public abstract void deserialize(String data);	
}
