package se.ugli.habanero.j.internal;

import se.ugli.habanero.j.TypedMap;
import se.ugli.habanero.j.TypedMapIterator;

public class TypedMapIdentityIterator extends TypedMapIterator<TypedMap> {

	@Override
	protected TypedMap nextObject(final TypedMap typedMap) {
		return typedMap;
	}

}