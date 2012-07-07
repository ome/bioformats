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
            
        function testDimensionOrder(self)
            
            I = uint8(rand(100, 100, 3, 4, 5) * (2^8-1));
            runDimensionOrderTest(I, self.path, 'XYZCT');
            runDimensionOrderTest(I, self.path, 'XYZTC');
            runDimensionOrderTest(I, self.path, 'XYCZT');
            runDimensionOrderTest(I, self.path, 'XYCTZ');
            runDimensionOrderTest(I, self.path, 'XYTZC');
            runDimensionOrderTest(I, self.path, 'XYTZC');
        end        
                
        
        function testPixelsType(self)

            I= rand(100, 100, 1, 1, 1) * (2^8-1);
            runPixelsTypeTest(uint8(I), self.path);
            runPixelsTypeTest(int8(I), self.path);
            runPixelsTypeTest(int16(I), self.path);
            runPixelsTypeTest(int16(I), self.path);
            runPixelsTypeTest(single(I), self.path);
            runPixelsTypeTest(double(I), self.path);
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