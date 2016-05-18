
package tletters.knnclassifier;

import java.util.Collection;

/**
 *
 * @author Piotr
 * @param <VAL>
 * @param <FV>
 * @param <DST>
 */
public interface Classifier <VAL extends Number, DST extends DistanceMeter<VAL> >
{
	public  void fit( Collection< Collection<VAL> > descriptions, Collection<Integer> classes, DST distancemeter );
	
	public Integer predict( Collection<VAL> description );
}
