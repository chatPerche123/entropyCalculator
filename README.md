# Entropy Calculator in Java
    What is an entropy ? It help to find out if something can be concidered random or not.
    Low entropy could be a compressed file and high entropy an obfuscated file like a malware or an encrypted files.

    To find it out in, you have to read each byte of the file.
    Each unique byte correspond to an occurrence.
    The probability of a particular byte opposed to the others is calculated by the
    division of the byte occurrency to the number of byte from the analysed file.

    Then for each element of an array that contain an unique byte as a key and his occurrency,
    we apply this formula:
       => P(x(h))*log2 p(x(h))

    "P(x(h))" is the probability of finding out a particular byte.

    In java, "log2 p(x(h))" is translated to:
       => Math.log(p(x(h)) / Math.log(2)

    Formula:
       => value = p(x(h) * (Math.log(p(x(h)) / Math.log(2))

    Now, that we have the entropy of a particular byte, we have to use the same logic for each
    bytes and increment the result with the previous one:

       => entropy = entropy + value*italic

![image](https://user-images.githubusercontent.com/58827656/130441459-0b2b0848-7959-46ff-a9bc-61d7d220d839.png)

