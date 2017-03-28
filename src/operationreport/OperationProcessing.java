package operationreport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.time.LocalDate;

public class OperationProcessing {

	List<String>opStringList = new ArrayList<String>();
	public static void main (String[] args){
		
		OperationProcessing op = new OperationProcessing();
		op.startProcess();
	}
	
	public void startProcess() {
		
		//Simulate incoming messages as strings
		for(int i = 0; i < 10; i++) {
			opStringList.add(new Instruction().getInstructionAsString());
		}
		
		opStringList.stream().forEach(System.out::println);
		
		//...And now rebuild two list (a third for Xmit errors) of Instructions
		List<Instruction>inList = new ArrayList<Instruction>();
		List<Instruction>outList = new ArrayList<Instruction>();
		
		List<Instruction>strangeList = new ArrayList<Instruction>();
		opStringList.stream().filter(s -> !s.contains("-S-") && !s.contains("-B-")).forEach(s -> strangeList.add(new Instruction(s)));

		inList = opStringList.stream().filter(s -> s.contains("-S-")).map(s -> new Instruction(s)).collect(Collectors.toList());
		outList = opStringList.stream().filter(s -> s.contains("-B-")).map(s -> new Instruction(s)).collect(Collectors.toList());
		
		Map<LocalDate, Double> daylyIn = inList.stream()
				.collect(Collectors.groupingBy(Instruction::getInsDate, 
						Collectors.summingDouble(Instruction::getInstrValue)));
		
		Map<LocalDate, Double> daylyOut = outList.stream()
				.collect(Collectors.groupingBy(Instruction::getInsDate, 
						Collectors.summingDouble(Instruction::getInstrValue)));
		
		System.out.println();
		System.out.println("Dayly Sell");
		System.out.println(daylyIn);
		
		System.out.println("Dayly Buy");
		System.out.println(daylyOut);
		
		Map<String, Optional<Instruction>> maxIn = inList.stream()
				.collect(Collectors.groupingBy(Instruction::getEntity, 
						Collectors.maxBy(Comparator.comparing( Instruction::getInstrValue))));
		
		Map<Double,String>rankIn = new TreeMap<Double,String>(Collections.reverseOrder());
		maxIn.forEach((k,v) -> rankIn.put(v.isPresent() ? v.get().getInstrValue() : 0.0, k));
		
		System.out.println();
		System.out.println("Entity Sell Rank");
		AtomicInteger count=new AtomicInteger(0);
		rankIn.forEach((k,v) -> System.out.println(count.incrementAndGet()+" "+v+" "+k));
		
		Map<String, Optional<Instruction>> maxOut = outList.stream()
				.collect(Collectors.groupingBy(Instruction::getEntity, 
						Collectors.maxBy(Comparator.comparing( Instruction::getInstrValue))));
		
		Map<Double,String>rankOut = new TreeMap<Double,String>(Collections.reverseOrder());
		maxOut.forEach((k,v) -> rankOut.put(v.isPresent() ? v.get().getInstrValue() : 0.0, k));
		
		System.out.println();
		System.out.println("Entity Buy Rank");
		count.set(0);
		rankOut.forEach((k,v) -> System.out.println(count.incrementAndGet()+" "+v+" "+k));
				
	}
	
}
