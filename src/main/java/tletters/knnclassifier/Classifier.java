package tletters.knnclassifier;

import java.util.Collection;

/**
 *
 * @author Piotr
 * @param <VAL>
 */
public interface Classifier <VAL extends Number>
{
	public  void fit( Collection< Collection<VAL> > descriptions, Collection<Integer> classes, DistanceMeter<VAL> distancemeter );
	
	public Integer predict( Collection<VAL> description );
}
