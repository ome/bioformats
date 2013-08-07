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
    end
    
    methods
        function self = TestBfGetPlane(name)
            self = self@TestBfMatlab(name);
        end
        
        function setUp(self)
            setUp@TestBfMatlab(self)
            bfCheckJavaPath();
            self.reader = loci.formats.in.FakeReader();
            self.sizeX = self.reader.DEFAULT_SIZE_X;
            self.sizeY = self.reader.DEFAULT_SIZE_Y;
        end
        
        function tearDown(self)
            self.reader.close()
            self.reader = [];
            tearDown@TestBfMatlab(self)
        end
        
        % Input check tests
        function checkInvalidInput(self, f)
            self.reader.setId('test.fake');
            assertExceptionThrown(f,...
                'MATLAB:InputParser:ArgumentFailedValidation');
        end
        
        function testReaderClass(self)
            self.checkInvalidInput(@() bfGetPlane(0, 1));
        end
        
        function testZeroPlane(self)
            self.checkInvalidInput(@() bfGetPlane(self.reader, 0));
        end
        
        function testOversizedPlaneIndex(self)
            self.checkInvalidInput(@() bfGetPlane(self.reader,...
                self.reader.getImageCount()+1));
        end
        
        function testPlaneIndexArray(self)
            self.checkInvalidInput(@() bfGetPlane(self.reader, [1 1]));
        end
        
        function testZeroX(self)
            self.checkInvalidInput(@() bfGetPlane(self.reader, 1, 0));
        end
        
        function testOversizedX(self)
            self.checkInvalidInput(@() bfGetPlane(self.reader, 1,...
                self.reader.getSizeX() +1));
        end
        
        function testZeroY(self)
            self.checkInvalidInput(@() bfGetPlane(self.reader, 1, 1, 0));
        end
        
        function testOversizedY(self)
            self.checkInvalidInput(@() bfGetPlane(self.reader, 1, 1,...
                self.reader.getSizeX() +1));
        end
        
        function testZeroWidth(self)
            self.checkInvalidInput(@() bfGetPlane(self.reader, 1, 1, 0));
        end
        
        function testOversizedWidth(self)
            self.checkInvalidInput(@() bfGetPlane(self.reader, 1, 1,...
                self.reader.getSizeX() +1));
        end
        
        function testZeroHeight(self)
            self.checkInvalidInput(@() bfGetPlane(self.reader, 1, 1, 1, 0));
        end
        
        function testOversizedHeight(self)
            self.checkInvalidInput(@() bfGetPlane(self.reader, 1, 1, 1, 1,...
                self.reader.getSizeX() +1));
        end
        
        % Pixel type tests
        function checkPixelsType(self, pixelsType)
            self.reader.setId([pixelsType '-test&pixelType=' pixelsType '.fake']);
            I = bfGetPlane(self.reader, 1);
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
            I = bfGetPlane(self.reader, 1);
            assertEqual(size(I, 2), self.sizeX);
            assertEqual(size(I, 1), self.sizeY);
        end
        
        function testSizeY(self)
            self.sizeY = 200;
            self.reader.setId(['sizeY-test&sizeY=' num2str(self.sizeY) '.fake']);
            I = bfGetPlane(self.reader, 1);
            assertEqual(size(I, 2), self.sizeX);
            assertEqual(size(I, 1), self.sizeY);
        end
        
        % Tile tests
        function checkTile(self, x, y, w, h)
            self.reader.setId('test.fake')
            I = bfGetPlane(self.reader, 1);
            I2 = bfGetPlane(self.reader, 1, x, y, w, h);
            
            assertEqual(I2, I(y : y + h - 1, x : x + w - 1));
        end
        
        function testFullTile(self)
            self.checkTile(1, 1, self.sizeX, self.sizeY)
        end
        
        function testSquareTile(self)
            self.checkTile(self.sizeX/4, self.sizeY/4,...
                self.sizeX/2, self.sizeY/2)
        end
        
        function testRectangularTile(self)
            self.checkTile(1, self.sizeY/4,...
                self.sizeX, self.sizeY/2);
        end
        
        function testSingleTile(self)
            self.checkTile(self.sizeX/2, self.sizeY/2, 1, 1);
        end
    end
end
