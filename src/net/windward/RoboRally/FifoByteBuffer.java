// code primarily from http://stackoverflow.com/questions/7122972/buffering-byte-data-in-c
// Edited by Windward Studios, Inc. (www.windward.net). No copyright claimed by Windward on changes.

package net.windward.RoboRally;

public class FifoByteBuffer
{
	private byte[] _buffer;
	private int _endIndex;
	private int _startIndex;

	public FifoByteBuffer(int capacity)
	{
		_buffer = new byte[capacity];
	}

	public final int getCount()
	{
		if (_endIndex > _startIndex)
			return _endIndex - _startIndex;
		if (_endIndex < _startIndex)
			return (_buffer.length - _startIndex) + _endIndex;
		return 0;
	}

	public final void Write(byte[] data)
	{
		Write(data, 0, data.length);
	}

	public final void Write(byte[] data, int offset, int length)
	{

		if (getCount() + length >= _buffer.length)
			throw new RuntimeException("buffer overflow");

		if (_endIndex + length >= _buffer.length)
		{
			int endLen = _buffer.length - _endIndex;
			int remainingLen = length - endLen;

			System.arraycopy(data, offset, _buffer, _endIndex, endLen);
			System.arraycopy(data, offset + endLen, _buffer, 0, remainingLen);
			_endIndex = remainingLen;
		}
		else
		{
			System.arraycopy(data, offset, _buffer, _endIndex, length);
			_endIndex += length;
		}
	}

	public final byte[] Read(int len)
	{
		return _Read(len, false);
	}

	public final byte[] Peek(int len)
	{
		return _Read(len, true);
	}

	private byte[] _Read(int len, boolean keepData)
	{
		if (len > getCount())
			throw new RuntimeException("not enough data in buffer");

		byte[] result = new byte[len];
		if (_startIndex + len < _buffer.length)
		{
			System.arraycopy(_buffer, _startIndex, result, 0, len);
			if (!keepData)
				_startIndex += len;
			return result;
		}
		int endLen = _buffer.length - _startIndex;
		int remainingLen = len - endLen;
		System.arraycopy(_buffer, _startIndex, result, 0, endLen);
		System.arraycopy(_buffer, 0, result, endLen, remainingLen);
		if (!keepData)
			_startIndex = remainingLen;
		return result;
	}
}