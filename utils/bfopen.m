function [result] = bfopen(id)

% portions of this code were adapted from:
% http://www.mathworks.com/support/solutions/data/1-2WPAYR.html?solution=1-2WPAYR

r = loci.formats.ChannelSeparator();
r.setId(id);
numSeries = r.getSeriesCount();
for s = 1:numSeries
    fprintf('Reading series #%d', s);
    r.setSeries(s - 1);
    w = r.getSizeX();
    h = r.getSizeY();
    numImages = r.getImageCount();
    arr = double(zeros([h, w, 1])); % use uint8 with imshow?
    for i = 0:numImages-1
        fprintf('.');
        img = r.openImage(i);
        % convert Java BufferedImage to MATLAB image
        B = img.getData.getPixels(0, 0, w, h, []);
        ndx = 1;
        for y = 1:h
            for x = 1:w
                arr(y,x,1) = B(ndx);
                ndx = ndx + 1;
            end
        end
        % plot the image plane in a new figure
        figure;
        pcolor(arr);
        % If you have the image processing toolbox, you could use:
        %imshow(arr);
        % You could do something else with each plane
        % here, rather than just overwriting the result.
        result = arr;
    end
    fprintf('\n');
end
