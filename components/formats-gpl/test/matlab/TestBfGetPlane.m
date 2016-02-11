% Tests for the bfGetPlane utility function
%
% Require MATLAB xUnit Test Framework to be installed
% http://www.mathworks.com/matlabcentral/fileexchange/22846-matlab-xunit-test-framework
% https://github.com/psexton/matlab-xunit (GitHub source code)

% OME Bio-Formats package for reading and converting biological file formats.
%
% Copyright (C) 2012 - 2016 Open Microscopy Environment:
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

classdef TestBfGetPlane < ReaderTest
    
    properties
        iPlane = 1
        x
        y
        width
        height
        bpp
        sgn
        fp
    end
    
    methods
        function self = TestBfGetPlane(name)
            self = self@ReaderTest(name);
        end
        
        function setUp(self)
            setUp@ReaderTest(self)
            self.reader.setId('test.fake');
            self.x = 1;
            self.y = 1;
            self.width = self.sizeX;
            self.height = self.sizeY;
        end
        
        % Input check tests
        function testNoInput(self)
            assertExceptionThrown(@() bfGetPlane(), 'MATLAB:minrhs');
        end
        
        function testReaderClass(self)
            assertExceptionThrown(@() bfGetPlane([]),...
                'MATLAB:InputParser:ArgumentFailedValidation');
        end
        
        function testInvalidReader(self)
            self.reader.close();
            assertExceptionThrown(@() bfGetPlane(self.reader),...
                'MATLAB:InputParser:ArgumentFailedValidation');
        end
        
        function testNoInputPlane(self)
            f = @() bfGetPlane(self.reader);
            assertExceptionThrown(f, 'MATLAB:InputParser:notEnoughInputs');
        end
        
        function checkInvalidInput(self)
            f = @() bfGetPlane(self.reader, self.iPlane);
            assertExceptionThrown(f,...
                'MATLAB:InputParser:ArgumentFailedValidation');
        end
        
        function testZeroPlane(self)
            assertExceptionThrown(@() bfGetPlane(self.reader, 0),...
                'MATLAB:InputParser:ArgumentFailedValidation');
        end
        
        function testOversizedPlaneIndex(self)
            nmax = self.reader.getImageCount();
            assertExceptionThrown(@() bfGetPlane(self.reader, nmax + 1),...
                'MATLAB:InputParser:ArgumentFailedValidation');
        end
        
        function testPlaneIndexArray(self)
            assertExceptionThrown(@() bfGetPlane(self.reader, [1 1]),...
                'MATLAB:InputParser:ArgumentFailedValidation');
        end
        
        %% Tile input tests
        function checkInvalidTileInput(self, varargin)
            f = @() bfGetPlane(self.reader, 1, varargin{:});
            assertExceptionThrown(f,...
                'MATLAB:InputParser:ArgumentFailedValidation');
        end
        
        function testZeroTileX(self)
            self.x = 0;
            self.checkInvalidTileInput(self.x)
        end
        
        function testOversizedTileX(self)
            self.x = self.sizeX + 1;
            self.checkInvalidTileInput(self.x);
        end
        
        function testZeroTileY(self)
            self.y = 0;
            self.checkInvalidTileInput(self.x, self.y);
        end
        
        function testOversizedTileY(self)
            self.y = self.sizeY + 1;
            self.checkInvalidTileInput(self.x, self.y);
        end
        
        function testZeroTileWidth(self)
            self.width = 0;
            self.checkInvalidTileInput(self.x, self.y, self.width);
        end
        
        function testOversizedTileWidth(self)
            self.width = self.sizeX + 1;
            self.checkInvalidTileInput(self.x, self.y, self.width);
        end
        
        function testOversizedTileWidth2(self)
            self.x = 2;
            self.width = self.sizeX;
            self.checkInvalidTileInput(self.x, self.y, self.width);
        end
        
        function testZeroTileHeight(self)
            self.height = 0;
            self.checkInvalidTileInput(self.x, self.y, self.width, self.height);
        end
        
        function testOversizedTileHeight(self)
            self.height = self.sizeY + 1;
            self.checkInvalidTileInput(self.x, self.y, self.width, self.height);
        end
        
        function testOversizedTileHeight2(self)
            self.y = 2;
            self.height = self.sizeY;
            self.checkInvalidTileInput(self.x, self.y, self.width, self.height);
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
        
        function testRectangularImageTile(self)
            self.sizeX = 100;
            self.reader.setId(['test&sizeX=' num2str(self.sizeX) '.fake']);
            self.y = 101;
            self.height = 1;
            self.width = 100;
            self.checkTile()
        end
        
        function testRectangularImageTile2(self)
            self.sizeY = 100;
            self.reader.setId(['test&sizeY=' num2str(self.sizeY) '.fake']);
            self.x = 101;
            self.width = 1;
            self.height = 100;
            self.checkTile()
        end
               
        function testJavaMethod(self)
            pixelType = self.reader.getPixelType();
            
            self.bpp = javaMethod('getBytesPerPixel', 'loci.formats.FormatTools', pixelType);
            self.fp = javaMethod('isFloatingPoint', 'loci.formats.FormatTools', pixelType);
            self.sgn = javaMethod('isSigned', 'loci.formats.FormatTools', pixelType);
            
            bpp = loci.formats.FormatTools.getBytesPerPixel(pixelType);
            fp = loci.formats.FormatTools.isFloatingPoint(pixelType);
            sgn = loci.formats.FormatTools.isSigned(pixelType);
            
            assertEqual(self.bpp, bpp);
            assertEqual(self.fp,fp);
            assertEqual(self.sgn,sgn);
            
        end

        
    end
end
