package dndcalculator.db;

import java.util.stream.Stream;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface DiceRepository extends CrudRepository<Dice, Integer> {
	
	@Query("SELECT name FROM Dice ORDER BY name")
	Stream<String> getDiceNames();
	
	Dice findByName(String name);

}
