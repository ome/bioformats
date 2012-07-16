% Integration tests for the bfSave utility function
%
% Require MATLAB xUnit Test Framework to be installed
% http://www.mathworks.com/matlabcentral/fileexchange/22846-matlab-xunit-test-framework

classdef TestBfsave < TestCase
    
    properties
        path
    end
    
    methods
        function self = TestBfsave(name)
            self = self@TestCase(name);
        end
        
        function setUp(self)
            if isunix,
                self.path = '/tmp/test.ome.tiff';
            else
                self.path = 'C:\test.ome.tiff';
            end
        end
        
        function tearDown(self)
            if exist(self.path,'file')==2, delete(self.path); end
        end
            
        % Dimension order tests
        function testDimensionOrderXYZCT(self)            
            I = uint8(rand(50, 100, 3, 4, 5) * (2^8-1));
            runDimensionOrderTest(I, self.path, 'XYZCT')
        end
        
        function testDimensionOrderXYZTC(self)            
            I = uint8(rand(50, 100, 3, 4, 5) * (2^8-1));
            runDimensionOrderTest(I, self.path, 'XYZTC')
        end
        
        function testDimensionOrderXYCZT(self)            
            I = uint8(rand(50, 100, 3, 4, 5) * (2^8-1));
            runDimensionOrderTest(I, self.path, 'XYCZT')
        end
        
        function testDimensionOrderXYCTZ(self)            
            I = uint8(rand(50, 100, 3, 4, 5) * (2^8-1));
            runDimensionOrderTest(I, self.path, 'XYCTZ')
        end
        function testDimensionOrderXYTCZ(self)            
            I = uint8(rand(50, 100, 3, 4, 5) * (2^8-1));
            runDimensionOrderTest(I, self.path, 'XYTCZ')
        end
        
        function testDimensionOrderXYTZC(self)            
            I = uint8(rand(50, 100, 3, 4, 5) * (2^8-1));
            runDimensionOrderTest(I, self.path, 'XYTZC')
        end
                
        % Data type tests
        function testPixelsTypeUINT8(self)
            I = uint8(rand(50, 100, 1, 1, 1) * (2^8-1));
            runPixelsTypeTest(I, self.path);
        end
        
        function testPixelsTypeINT8(self)
            I = int8(rand(50, 100, 1, 1, 1) * (2^8-1));
            runPixelsTypeTest(I, self.path);
        end
        
        function testPixelsTypeUINT16(self)
            I = uint16(rand(50, 100, 1, 1, 1) * (2^16-1));
            runPixelsTypeTest(I, self.path);
        end
        
        function testPixelsTypeINT16(self)
            I = int16(rand(50, 100, 1, 1, 1) * (2^16-1));
            runPixelsTypeTest(I, self.path);
        end
        
        function testPixelsTypeUINT32(self)
            I = uint32(rand(50, 100, 1, 1, 1) * (2^32-1));
            runPixelsTypeTest(I, self.path);
        end
        
        function testPixelsTypeINT32(self)
            I = int32(rand(50, 100, 1, 1, 1) * (2^32-1));
            runPixelsTypeTest(I, self.path);
        end
        
        function testPixelsTypeFLOAT(self)
            I = single(rand(50, 100, 1, 1, 1) * (2^16-1));
            runPixelsTypeTest(I, self.path);
        end
        
        function testPixelsTypeDOUBLE(self)
            I= double(rand(50, 100, 1, 1, 1) * (2^16-1));
            runPixelsTypeTest(I, self.path);
        end
    end
    
end

function runDimensionOrderTest(I, path, dimensionOrder)

% Create stack and save it
bfsave(I, path, dimensionOrder);
sizeZ = size(I,find(dimensionOrder=='Z'));
sizeC = size(I,find(dimensionOrder=='C'));
sizeT = size(I,find(dimensionOrder=='T'));

% Check dimensions of saved ome-tiff
r = bfGetReader(path);
assertEqual(r.getSizeZ, sizeZ);
assertEqual(r.getSizeC, sizeC);
assertEqual(r.getSizeT, sizeT);

% Test all planes
for iPlane = 1 : sizeZ * sizeC * sizeT
    [i,j,k] = ind2sub([size(I, 3) size(I, 4) size(I, 5)], iPlane);
    assertEqual(I(:, :, i, j, k), bfGetPlane(r, iPlane));
end
end

function runPixelsTypeTest(I, path)

% Create stack and save it
bfsave(I, path);

r = bfGetReader(path);
assertEqual(I, bfGetPlane(r,1));

end