// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.persistence.typeHandling.bytebuffer;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.TFloatList;
import gnu.trove.list.TIntList;
import gnu.trove.list.TLongList;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.list.array.TLongArrayList;
import org.terasology.persistence.typeHandling.DeserializationException;
import org.terasology.persistence.typeHandling.PersistedData;
import org.terasology.persistence.typeHandling.PersistedDataArray;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class ByteBufferPersistedDataArray extends ByteBufferPersistedData implements PersistedDataArray {

    private final BBArrayType arrayType;
    private final int size;

    public ByteBufferPersistedDataArray(ByteBuffer byteBuffer) {
        super(byteBuffer);
        arrayType = BBArrayType.parse(byteBuffer.get());
        size = byteBuffer.getInt();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public PersistedData getArrayItem(int index) {
        BBType primitiveType = arrayType.getPrimitiveType();
        if (primitiveType != null) {
            return new ByteBufferPersistedData(byteBuffer, calculateIndex(index), primitiveType.getCode());
        } else {
            return new ByteBufferPersistedData(byteBuffer, calculateIndex(index));
        }
    }

    @Override
    public boolean isNumberArray() {
        return arrayType == BBArrayType.FLOAT
                || arrayType == BBArrayType.DOUBLE
                || arrayType == BBArrayType.INTEGER
                || arrayType == BBArrayType.LONG;
    }

    @Override
    public boolean isBooleanArray() {
        return arrayType == BBArrayType.BOOLEAN;
    }

    @Override
    public boolean isStringArray() {
        return arrayType == BBArrayType.STRING;
    }

    @Override
    public List<String> getAsStringArray() {
        List<String> list = new ArrayList<>(size());
        for (int i = 0; i < size(); i++) {
            list.add(getArrayItem(i).getAsString());
        }
        return list;
    }

    @Override
    public String getAsString() {
        if (isStringArray()) {
            if (size() == 1) {
                return getArrayItem(0).getAsString();
            } else {
                throw new IllegalStateException("String array have size != 1");
            }
        } else {
            if (size() == 1) {
                PersistedData data = getArrayItem(0);
                if (data.isString()) {
                    return data.getAsString();
                }
            }
            throw new ClassCastException("it is not string array");
        }
    }


    @Override
    public double getAsDouble() {
        if (isNumberArray()) {
            if (size() == 1) {
                return getArrayItem(0).getAsDouble();
            } else {
                throw new IllegalStateException("Number array have size != 1");
            }
        } else {
            if (size() == 1) {
                PersistedData data = getArrayItem(0);
                if (data.isNumber()) {
                    return data.getAsDouble();
                }
            }
            throw new ClassCastException("it is not number array");
        }
    }

    @Override
    public float getAsFloat() {
        if (isNumberArray()) {
            if (size() == 1) {
                return getArrayItem(0).getAsFloat();
            } else {
                throw new IllegalStateException("Number array have size != 1");
            }
        } else {
            if (size() == 1) {
                PersistedData data = getArrayItem(0);
                if (data.isNumber()) {
                    return data.getAsFloat();
                }
            }
            throw new ClassCastException("it is not number array");
        }
    }

    @Override
    public int getAsInteger() {
        if (isNumberArray()) {
            if (size() == 1) {
                return getArrayItem(0).getAsInteger();
            } else {
                throw new IllegalStateException("Number array have size != 1");
            }
        } else {
            if (size() == 1) {
                PersistedData data = getArrayItem(0);
                if (data.isNumber()) {
                    return data.getAsInteger();
                }
            }
            throw new ClassCastException("it is not number array");
        }
    }

    @Override
    public long getAsLong() {
        if (isNumberArray()) {
            if (size() == 1) {
                return getArrayItem(0).getAsLong();
            } else {
                throw new IllegalStateException("Number array have size != 1");
            }
        } else {
            if (size() == 1) {
                PersistedData data = getArrayItem(0);
                if (data.isNumber()) {
                    return data.getAsLong();
                }
            }
            throw new ClassCastException("it is not number array");
        }
    }

    @Override
    public TDoubleList getAsDoubleArray() {
        byteBuffer.position(position + 6);
        TDoubleList list = new TDoubleArrayList(size());
        Iterator<PersistedData> iter = typedIterator(arrayType.getPrimitiveType());
        while (iter.hasNext()) {
            list.add(iter.next().getAsDouble());
        }
        return list;
    }

    @Override
    public TFloatList getAsFloatArray() {
        byteBuffer.position(position + 6);
        TFloatList list = new TFloatArrayList(size());
        Iterator<PersistedData> iter = typedIterator(arrayType.getPrimitiveType());
        while (iter.hasNext()) {
            list.add(iter.next().getAsFloat());
        }
        return list;
    }

    @Override
    public TIntList getAsIntegerArray() {
        byteBuffer.position(position + 6);
        TIntList list = new TIntArrayList(size());
        Iterator<PersistedData> iter = typedIterator(arrayType.getPrimitiveType());
        while (iter.hasNext()) {
            list.add(iter.next().getAsInteger());
        }
        return list;
    }

    @Override
    public TLongList getAsLongArray() {
        byteBuffer.position(position + 6);
        TLongList list = new TLongArrayList(size());
        Iterator<PersistedData> iter = typedIterator(arrayType.getPrimitiveType());
        while (iter.hasNext()) {
            list.add(iter.next().getAsLong());
        }
        return list;
    }

    @Override
    public boolean[] getAsBooleanArray() {
        byteBuffer.position(position + 6);
        int sizeInBytes = size() % 8 + 1;
        byte[] bytes = new byte[sizeInBytes];
        byteBuffer.get(bytes);
        boolean[] booleans = new boolean[size()];
        for (int i = 0; i < sizeInBytes; i++) {
            for (int bi = 0; bi < 8; bi++) {
                if (i * 8 + bi >= size()) {
                    break;
                }
                booleans[i * 8 + bi] = ((bytes[i] >> bi) & 1) == 1;
            }
        }
        return booleans;
    }

    @Override
    public List<PersistedData> getAsValueArray() {
        byteBuffer.position(position + 6);
        List<PersistedData> data = new ArrayList<>(size());
        for (int i = 0; i < size(); i++) {
            data.add(new ByteBufferPersistedData(byteBuffer, calculateIndex(i)));
        }
        return data;
    }

    @Override
    public Iterator<PersistedData> iterator() {
        return new Iterator<PersistedData>() {
            private int index;

            @Override
            public boolean hasNext() {
                return index < size;
            }

            @Override
            public PersistedData next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("iterator haven't something.");
                }
                PersistedData data = new ByteBufferPersistedData(byteBuffer, calculateIndex(index));
                index++;
                return data;
            }
        };
    }

    private Iterator<PersistedData> typedIterator(BBType type) {
        return new Iterator<PersistedData>() {
            private int index;

            @Override
            public boolean hasNext() {
                return index < size;
            }

            @Override
            public PersistedData next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("iterator haven't something.");
                }
                PersistedData data = new ByteBufferPersistedData(byteBuffer, calculateIndex(index), type.getCode());
                index++;
                return data;
            }
        };
    }

    private int calculateIndex(int index) {
        switch (arrayType) {
            case BOOLEAN:
                return 6 + index % 8 + 1;
            case FLOAT:
            case INTEGER:
                return 6 + index * 4;
            case DOUBLE:
            case LONG:
                return 6 + index * 8;
            case STRING: {
                int pos = position + 6;
                for (int i = 0; i < index; i++) {
                    pos += byteBuffer.getInt(pos) + 4;
                }
                return pos;
            }
            case VALUE: {
                int pos = 0;
                for (int i = 0; i < index; i++) {
                    pos += byteBuffer.getInt(position + 6 + i * 4);
                }
                int sizeArraySize = size() * 4;
                return pos + position + 6 + sizeArraySize;
            }

        }
        throw new UnsupportedOperationException("IDK how it to do");
    }

    @Override
    public boolean getAsBoolean() {
        if (isBooleanArray()) {
            if (size() == 1) {
                return (byteBuffer.get(position + 6) & 1) == 1;
            } else {
                throw new IllegalStateException("boolean array have size != 1");
            }
        } else {
            if (size() == 1) {
                PersistedData data = getArrayItem(0);
                if (data.isBoolean()) {
                    return data.getAsBoolean();
                }
            }
            throw new ClassCastException("it is not boolean array");
        }
    }

    @Override
    public byte[] getAsBytes() {
        if (size() == 1) {
            PersistedData data = getArrayItem(0);
            if (data.isBytes()) {
                return data.getAsBytes();
            }
        }
        throw new DeserializationException("it is not bytes array");
    }

    @Override
    public ByteBuffer getAsByteBuffer() {
        if (size() == 1) {
            PersistedData data = getArrayItem(0);
            if (data.isBytes()) {
                return data.getAsByteBuffer();
            }
        }
        throw new DeserializationException("it is not bytes array");
    }
}
