
package tletters.knnclassifier;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author Piotr
 * @param <VAL>
 */
public class KNNClassifier <VAL  extends Number> 
implements Classifier <VAL, DistanceMeter<VAL> >
{	
	
	private Collection<Collection<VAL>> m_descriptions;
	Collection<Integer> m_classes;
	private DistanceMeter<VAL> m_distancemeter;
	private int m_K;
	
	public KNNClassifier( int K )
	{
		m_K = K;
	}

	@Override
	public void fit(Collection<Collection<VAL>> descriptions, 
		Collection<Integer> classes, DistanceMeter<VAL> distancemeter)
	{
		m_descriptions = descriptions;
		m_classes = classes;
		m_distancemeter = distancemeter;
	}


	@Override
	public Integer predict(Collection<VAL> description)
	{
		SortedSet<Pair> neighbours = new TreeSet<>( 
		(Pair o1, Pair o2)->o1.distance.compareTo(o2.distance) );
		
		Iterator< Collection<VAL> > it_fv = m_descriptions.iterator();
		Iterator< Integer > it_class = m_classes.iterator();
		
		double dist;
		int objclass;
		while( it_fv.hasNext() && it_class.hasNext() )
		{
			dist = m_distancemeter.distance(description, it_fv.next() );
			objclass = it_class.next();
			
			if( neighbours.size() < m_K)
			{
				neighbours.add( new Pair(dist, objclass) );
			}
			else if( dist < neighbours.last().distance )
			{
				neighbours.add( new Pair(dist, objclass) );
				
				//can be ommited, but it changes complexity from O(n*logn) to O(n*logK)
				neighbours.remove( neighbours.last() );
			}
		}
		
		// count classes 
		HashMap<Integer, int[]> classCounter = new HashMap<>();
		for( Pair p : neighbours)
		{
			int[] valueWrapper = classCounter.get(p.objclass);
			if (valueWrapper == null) 
			{
				classCounter.put(p.objclass, new int[] { 1 });
			} else 
			{
				valueWrapper[0]++;
			}
		}
		
		// find most common class
		int maxCount = 1;
		int commonClass = neighbours.first().objclass;
		for( Pair p : neighbours)
		{
			int[] valueWrapper = classCounter.get(p.objclass);
			if ( valueWrapper[0] > maxCount )
			{
				maxCount = valueWrapper[0];
				commonClass = p.objclass;
			}
		}
		
		// might be different due to solving cases of equal number of neighbour classes
		return commonClass;
	}
	
	private class Pair
	{
		public Double distance;
		public int objclass;
		public Pair( double dist, int objcl)
		{
			this.distance = dist;
			this.objclass = objcl;
		}
	};

}
