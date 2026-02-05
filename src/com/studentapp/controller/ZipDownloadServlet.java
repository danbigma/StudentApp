package com.studentapp.controller;

import java.io.*;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

@WebServlet("/zipservlet")
public class ZipDownloadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ZipDownloadServlet.class);

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// The path below is the root directory of data to be compressed.
		String path = getServletContext().getRealPath("css");
		File directory = new File(path);
		String[] files = directory.list();

		// Checks to see if the directory contains some files.
		if (files != null && files.length > 0) {
			// Sends the response back to the user / browser. The
			// content for zip file type is "application/zip". We
			// also set the content disposition as attachment for
			// the browser to show a dialog that will let user
			// choose what action will he do to the sent content.
			response.setContentType("application/zip");
			response.setHeader("Content-Disposition", "attachment; filename=data.zip");

			// Stream the zip directly to the response output stream to save memory
			try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {
				zipFiles(directory, files, zos);
			} catch (IOException e) {
				// This can happen if the client closes the connection. Log as a warning.
				logger.warn("IOException during zip streaming, client may have aborted connection.", e);
			}
		} else {
			// No files to zip, send an empty response or an error.
			// Sending a 204 No Content is appropriate.
			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
		}
	}

	/**
	 * Compress the given files from a directory into the ZipOutputStream.
	 */
	private void zipFiles(File directory, String[] files, ZipOutputStream zos) throws IOException {
		byte[] buffer = new byte[4096];

		for (String fileName : files) {
			File fileToZip = new File(directory, fileName);
			// Skip directories or non-existent files
			if (!fileToZip.exists() || !fileToZip.isFile()) {
				continue;
			}
			zos.putNextEntry(new ZipEntry(fileName));
			// Use try-with-resources to ensure the FileInputStream is closed
			try (FileInputStream fis = new FileInputStream(fileToZip)) {
				int length;
				while ((length = fis.read(buffer)) >= 0) {
					zos.write(buffer, 0, length);
				}
			}
			zos.closeEntry();
		}
	}
}
