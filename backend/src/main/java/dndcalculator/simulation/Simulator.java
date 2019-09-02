package dndcalculator.simulation;

import dndcalculator.model.Roll;
import dndcalculator.model.Throw;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Simulator {
	
	private final Roll attackRoll;
	private final Roll damageRoll;
	private final int armorClass;
	
	public int doAttack() {
		Throw attackThrow = attackRoll.doThrow();
		
		if (isCritFail(attackThrow)) {
			return 0;
		} else if (isCritHit(attackThrow)) {
			return calculateCritDamage(damageRoll);
		} else if (isHit(attackThrow, armorClass)) {
			return calculateDamage(damageRoll);
		}
		
		return 0;
	}
	
	private static boolean isCritFail(Throw attack) {
		return attack.getBase() == 1;
	}
	
	private static boolean isCritHit(Throw attack) {
		return attack.getBase() >= 19;
	}
	
	private static boolean isHit(Throw attack, int ac) {
		return attack.getTotal() >= ac;
	}
	
	private static int calculateDamage(Roll damage) {
		return damage.throwDice();
	}
	
	private static int calculateCritDamage(Roll damage) {
		return damage.doThrow().getBase() + damage.throwDice();
	}

}
