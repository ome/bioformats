% Tests for the bfGetPlane utility function
%
% Require MATLAB xUnit Test Framework to be installed
% http://www.mathworks.com/matlabcentral/fileexchange/22846-matlab-xunit-test-framework

% OME Bio-Formats package for reading and converting biological file formats.
%
% Copyright (C) 2012 - 2013 Open Microscopy Environment:
%   - Board of Regents of the University of Wisconsin-Madison
%   - Glencoe Software, Inc.
%   - University of Dundee
%
% This program is free software: you can redistribute it and/or modify
% it under the terms of the GNU General Public License as
% published by the Free Software Foundation, either version 2 of the
% License, or (at your option) any later version.
%
% This program is distributed in the hope that it will be useful,
% but WITHOUT ANY WARRANTY; without even the implied warranty of
% MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
% GNU General Public License for more details.
%
% You should have received a copy of the GNU General Public License along
% with this program; if not, write to the Free Software Foundation, Inc.,
% 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

classdef TestBfGetPlane < TestBfMatlab
    
    properties
        reader
        sizeX
        sizeY
        iPlane = 1
        x
        y
        width
        height
    end
    
    methods
        function self = TestBfGetPlane(name)
            self = self@TestBfMatlab(name);
        end
        
        function setUp(self)
            setUp@TestBfMatlab(self)
            bfCheckJavaPath();
            self.reader = loci.formats.in.FakeReader();
            self.reader.setId('test.fake');
            self.sizeX = self.reader.DEFAULT_SIZE_X;
            self.sizeY = self.reader.DEFAULT_SIZE_Y;
        end
        
        function tearDown(self)
            self.reader.close()
            self.reader = [];
            tearDown@TestBfMatlab(self)
        end
        
        % Input check tests
        function testReaderClass(self)
            assertExceptionThrown(@() bfGetPlane(0, self.iPlane),...
                'MATLAB:InputParser:ArgumentFailedValidation');
        end
        
        function checkInvalidInput(self)
            f = @() bfGetPlane(self.reader, self.iPlane);
            assertExceptionThrown(f,...
                'MATLAB:InputParser:ArgumentFailedValidation');
        end
        
        function testZeroPlane(self)
            self.iPlane = 0;
            self.checkInvalidInput();
        end
        
        function testOversizedPlaneIndex(self)
            self.iPlane = self.reader.getImageCount()+1;
            self.checkInvalidInput();
        end
        
        function testPlaneIndexArray(self)
            self.iPlane = [1 1];
            self.checkInvalidInput();
        end
        
        function checkInvalidTileInput(self)
            f = @() bfGetPlane(self.reader, 1, self.x, self.y,...
                self.width, self.height);
            assertExceptionThrown(f,...
                'MATLAB:InputParser:ArgumentFailedValidation');
        end
        
        function testZeroTileX(self)
            self.x = 0;
            self.checkInvalidTileInput()
        end
        
        function testOversizedTileX(self)
            self.x = self.sizeX + 1;
            self.checkInvalidTileInput();
        end
        
        function testZeroTileY(self)
            self.x = 1;
            self.y = 0;
            self.checkInvalidTileInput();
        end
        
        function testOversizedTileY(self)
            self.x = 1;
            self.y = self.sizeY + 1;
            self.checkInvalidTileInput();
        end
        
        function testZeroTileWidth(self)
            self.x = 1;
            self.y = 1;
            self.width = 0;
            self.checkInvalidTileInput();
        end
        
        function testOversizedTileWidth(self)
            self.x = 1;
            self.y = 1;
            self.width = self.sizeX + 1;
            self.checkInvalidTileInput();
        end
        
        function testZeroTileHeight(self)
            self.x = 1;
            self.y = 1;
            self.width = self.sizeX;
            self.height = 0;
            self.checkInvalidTileInput();
        end
        
        function testOversizedTileHeight(self)
            self.x = 1;
            self.y = 1;
            self.width = self.sizeX;
            self.height = self.sizeY + 1;
            self.checkInvalidTileInput();
        end
        
        % Pixel type tests
        function checkPixelsType(self, pixelsType)
            self.reader.setId([pixelsType '-test&pixelType=' pixelsType '.fake']);
            I = bfGetPlane(self.reader, self.iPlane);
            if strcmp(pixelsType, 'float')
                assertEqual(class(I), 'single');
            else
                assertEqual(class(I), pixelsType);
            end
        end
        
        function testINT8(self)
            self.checkPixelsType('int8');
        end
        function testUINT8(self)
            self.checkPixelsType('uint8');
        end
        function testINT16(self)
            self.checkPixelsType('int16');
        end
        function testUINT16(self)
            self.checkPixelsType('uint16');
        end
        function testINT32(self)
            self.checkPixelsType('int32');
        end
        function testUINT32(self)
            self.checkPixelsType('uint32');
        end
        function testSINGLE(self)
            self.checkPixelsType('float');
        end
        function testDOUBLE(self)
            self.checkPixelsType('double');
        end
        
        % Dimension size tests
        function testSizeX(self)
            self.sizeX = 200;
            self.reader.setId(['sizeX-test&sizeX=' num2str(self.sizeX) '.fake']);
            I = bfGetPlane(self.reader, self.iPlane);
            assertEqual(size(I, 2), self.sizeX);
            assertEqual(size(I, 1), self.sizeY);
        end
        
        function testSizeY(self)
            self.sizeY = 200;
            self.reader.setId(['sizeY-test&sizeY=' num2str(self.sizeY) '.fake']);
            I = bfGetPlane(self.reader, self.iPlane);
            assertEqual(size(I, 2), self.sizeX);
            assertEqual(size(I, 1), self.sizeY);
        end
        
        % Tile tests
        function checkTile(self)
            % Retrieve full plane and tile
            I = bfGetPlane(self.reader, self.iPlane);
            I2 = bfGetPlane(self.reader, self.iPlane, self.x, self.y,...
                self.width, self.height);
            
            assertEqual(I2, I(self.y : self.y + self.height - 1,...
                self.x : self.x + self.width - 1));
        end
        
        function testFullTile(self)
            self.x = 1;
            self.y = 1;
            self.width = self.sizeX;
            self.height = self.sizeY;
            self.checkTile()
        end
        
        function testSquareTile(self)
            self.x = self.sizeX / 4;
            self.y = self.sizeY / 4;
            self.width = self.sizeX / 2;
            self.height = self.sizeY / 2;
            self.checkTile()
        end
        
        function testRectangularTile(self)
            self.x = 1;
            self.y = self.sizeY / 4;
            self.width = self.sizeX;
            self.height = self.sizeY / 2;
            self.checkTile()
        end
        
        function testSingleTile(self)
            self.x = self.sizeX / 2;
            self.y = self.sizeY / 4;
            self.width = 1;
            self.height = 1;
            self.checkTile()
        end
    end
end
