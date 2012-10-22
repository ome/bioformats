% Tests for the bfGetPlane utility function
%
% Require MATLAB xUnit Test Framework to be installed
% http://www.mathworks.com/matlabcentral/fileexchange/22846-matlab-xunit-test-framework

classdef TestBfGetPlane < TestCase
    
    properties
        reader
        sizeX
        sizeY
    end
    
    methods
        function self = TestBfGetPlane(name)
            self = self@TestCase(name);
        end
        
        function setUp(self)
            self.reader = loci.formats.in.FakeReader();
            self.sizeX =  self.reader.DEFAULT_SIZE_X;
            self.sizeY =  self.reader.DEFAULT_SIZE_Y;
            self.reader = loci.formats.ChannelSeparator(self.reader);
        end
        
        function tearDown(self)
            self.reader.close();
        end
        
        % Pixel type tests
        function testInt8(self)
            self.reader.setId('int8-test&pixelType=int8.fake')
            I = bfGetPlane(self.reader, 1);
            assertEqual(class(I), 'int8');
            assertEqual(size(I), [self.sizeY, self.sizeX]);
        end
        
        function testUInt8(self)
            self.reader.setId('uint8-test&pixelType=uint8.fake')
            I = bfGetPlane(self.reader, 1);
            assertEqual(class(I),'uint8');
            assertEqual(size(I), [self.sizeY, self.sizeX]);
        end
        
        function testInt16(self)
            self.reader.setId('int16-test&pixelType=int16.fake')
            I = bfGetPlane(self.reader, 1);
            assertEqual(class(I),'int16');
            assertEqual(size(I), [self.sizeY, self.sizeX]);
        end
        
        function testUInt16(self)
            self.reader.setId('uint16-test&pixelType=uint16.fake')
            I = bfGetPlane(self.reader, 1);
            assertEqual(class(I),'uint16');
            assertEqual(size(I), [self.sizeY, self.sizeX]);
        end
        
        function testInt32(self)
            self.reader.setId('int32-test&pixelType=int32.fake')
            I = bfGetPlane(self.reader, 1);
            assertEqual(class(I),'int32');
            assertEqual(size(I), [self.sizeY, self.sizeX]);
        end
        
        function testUInt32(self)
            self.reader.setId('uint32-test&pixelType=uint32.fake')
            I = bfGetPlane(self.reader, 1);
            assertEqual(class(I),'uint32');
        end
        
        function testSingle(self)
            self.reader.setId('float-test&pixelType=float.fake')
            I = bfGetPlane(self.reader, 1);
            assertEqual(class(I),'single');
            assertEqual(size(I), [self.sizeY, self.sizeX]);
        end
        
        function testDouble(self)
            self.reader.setId('double-test&pixelType=double.fake')
            I = bfGetPlane(self.reader, 1);
            assertEqual(class(I),'double');
            assertEqual(size(I), [self.sizeY, self.sizeX]);
        end
        
    end
end
