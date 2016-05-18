package tletters.knnclassifier;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Piotr
 */
public class EuclideanDistanceMeter<VAL extends Number> implements DistanceMeter<VAL> {

    @Override
    public double distance(Collection<VAL> lhs, Collection<VAL> rhs) {
        Iterator<VAL> it_lhs = lhs.iterator();
        Iterator<VAL> it_rhs = rhs.iterator();
        double result = 0.0;
        double diff;
        while (it_lhs.hasNext() && it_rhs.hasNext()) {
            diff = it_lhs.next().doubleValue() - it_rhs.next().doubleValue();
            result += diff * diff;
        }
//		becouse of the fact that distances are only compared to each other in knn algorithm we can omit commputing sqrt
//		return Math.sqrt(result);
        return result;
    }

}
