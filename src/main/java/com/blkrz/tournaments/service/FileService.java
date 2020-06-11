package com.blkrz.tournaments.service;

import com.blkrz.tournaments.exception.TournamentsFileException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Transactional
@Service
public class FileService
{
    private static final Logger logger = LogManager.getLogger(FileService.class);

    private static final int MAX_FILE_SIZE_IN_BYTES = 1_000_000;

    /** @return path to saved file. */
    public String saveUploadedImage(MultipartFile file, String pathToUploadsDirectory) throws TournamentsFileException
    {
        checkImage(file);

        if (!new File(pathToUploadsDirectory).exists())
        {
            logger.log(Level.DEBUG, "Directory " + pathToUploadsDirectory + " doesn't exist, creating one.");
            Path directory;
            try
            {
                directory = Files.createDirectories(Paths.get(pathToUploadsDirectory));
            }
            catch (IOException e)
            {
                e.printStackTrace();
                throw new TournamentsFileException("Can't create directory to upload files.", e);
            }
        }

        try
        {
            String orgName = file.getOriginalFilename();
            String filePath = pathToUploadsDirectory + orgName;
            Path dest = Files.createFile(Path.of(filePath));
            file.transferTo(dest);

            logger.log(Level.INFO, "Saved file " + filePath);
            return filePath;
        }
        catch (IOException e)
        {
            throw new TournamentsFileException("Cannot save image due to IO exception.", e);
        }
    }

    private void checkImage(MultipartFile file) throws TournamentsFileException
    {
        if (file.isEmpty())
        {
            throw new TournamentsFileException("File is empty.");
        }

        if (file.getSize() > MAX_FILE_SIZE_IN_BYTES)
        {
            throw new TournamentsFileException("File is too big, max allowed file size is " + (MAX_FILE_SIZE_IN_BYTES * 0.001) + "kB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !(contentType.equals("image/jpeg") || contentType.equals("image/png")))
        {
            throw new TournamentsFileException("Only jpeg and png images are allowed. Used: " + contentType);
        }
    }

    public byte[] getSavedImage(String filePath) throws TournamentsFileException
    {
        Path path = Paths.get(filePath);

        try
        {
            return Files.readAllBytes(path);
        }
        catch (IOException e)
        {
            throw new TournamentsFileException("Can't read file due to IO exception.", e);
        }
    }
}
