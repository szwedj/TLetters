
package tletters.knnclassifier;

import java.util.Collection;

/**
 *
 * @author Piotr
 * @param <VAL>
 */
public interface  DistanceMeter <VAL extends Number>
{
	public  double distance( Collection<VAL> lhs, Collection<VAL> rhs );
}
