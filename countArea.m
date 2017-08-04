function countArea();
I=imread('C:\xampp\htdocs\news\upload\leaf.jpg');%path of the directory which the uploaded image is saved
Igray = rgb2gray(I); %digital image is coverted to gray scale image

Igray = imadjust(Igray); %increasing the contrast of the gray scale image

level = graythresh(Igray);  %# Compute an appropriate threshold

BW = im2bw(Igray,level); %convert gray scale image to black and white image

invert=imcomplement(BW); %get the invert of the black and white image

BW2=imfill(invert,'holes'); % fill spaces of the black and white image with white


bw3 = bwareafilt(BW2,2); % get the two largest objects-->leaf+reference object


bw4 = bwareafilt(BW2,1); % get the largest object-->leaf
figure;
imshow(bw4);

leafPixels = sum(bw4(:)); %get the pixel count of the leaf


totalPixels = sum(bw3(:)); %get the pixel count of the leaf and the reference object


squarePixels = totalPixels - leafPixels; % get the pixcel count of the reference object of 1 cm^2


leafArea = leafPixels/squarePixels; %calculate the leaf area by getting the ratio

fprintf('Area of the leaf is %f cm^2\n',leafArea);



file=fopen('C:\xampp\htdocs\news\upload\area.txt','w');% write the calculated area to a text file in the given path

%fprintf(file,'Area of the leaf:%f cm^2',leafArea);
fprintf(file,'%f',leafArea);

quit force
