package dndcalculator.db;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Entity
@Data
public class Dice {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Setter(AccessLevel.NONE)
	private Integer id;
	private String name;
	private String primitiveType;
	private String obj;
	private String texture;
	private String textureCoords;

}
