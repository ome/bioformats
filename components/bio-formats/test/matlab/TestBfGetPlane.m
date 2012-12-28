% Tests for the bfGetPlane utility function
%
% Require MATLAB xUnit Test Framework to be installed
% http://www.mathworks.com/matlabcentral/fileexchange/22846-matlab-xunit-test-framework

classdef TestBfGetPlane < TestCase
    
    properties
        reader
        sizeX
        sizeY
        x
        y
        w
        h
    end
    
    methods
        function self = TestBfGetPlane(name)
            self = self@TestCase(name);
        end
        
        function setUp(self)
            bfCheckJavaPath();
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
        
        % Tile tests
        function testFullTile(self)
            self.reader.setId('sqtile-test&pixelType=int8.fake')
            self.x = 1;
            self.y = 1;
            self.w = self.reader.getSizeX();
            self.h = self.reader.getSizeY();
            self.checkTile()
        end
        
        function testSquareTile(self)
            self.reader.setId('sqtile-test&pixelType=int8.fake')
            self.x = 10;
            self.y = 10;
            self.w = 20;
            self.h = 20;
            self.checkTile()
        end
        
        function testRectangularTile(self)
            self.reader.setId('recttile-test&pixelType=int8.fake')
            self.x = 20;
            self.y = 10;
            self.w = 40;
            self.h = 20;
            self.checkTile();
        end
        
        function checkTile(self)
            I = bfGetPlane(self.reader, 1);
            I2 = bfGetPlane(self.reader, 1, self.x, self.y, self.w, self.h);

            assertEqual(I2, I(self.y : self.y + self.h - 1,...
                self.x : self.x + self.w - 1));
        end
    end
    
end
