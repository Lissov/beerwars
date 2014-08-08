package com.pl.beerwars.ArtIntelligence.Tasky;
import com.pl.beerwars.ArtIntelligence.*;
import java.util.*;

public class TaskyState extends State
{
	public HashMap<String, HashMap<Integer, Float>> priceSteps
		= new HashMap<String, HashMap<Integer, Float>>();
	
	public List<Task> tasks = new LinkedList<Task>();

	@Override
	public String serialize()
	{
		StateSerializer s = new StateSerializer();
		s.writeMap(priceSteps, "pst");
		s.write("task_cnt", tasks.size());
		int num = 0;
		for (Task t : tasks){
			t.serialize(s, "task_"+num);
			num++;
		}
		return s.getString();
	}

	@Override
	public void deserialize(String data)
	{
		StateSerializer s = new StateSerializer(data);
		priceSteps = s.readHashMap("pst");
		int cnt = s.readValueInt("task_cnt", 0);
		tasks = new LinkedList<Task>();
		for (int num = 0; num < cnt; num++){
			int taskT = s.readValueInt("task_"+num + "_type", -1);
			Task t = TaskHelper.getTask(taskT);
			t.deserialize(s, "task_"+num);
			tasks.add(t);
		}
	}
}
