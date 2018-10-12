package com.brent.databasemanager.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.springframework.stereotype.Service;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.brent.databasemanager.pojo.ObjectStorageException;
import com.brent.databasemanager.pojo.TableContents;

@Service
public class S3Manager {
	public String UploadFile(TableContents tc)
	{
		AmazonS3 client = AmazonS3ClientBuilder.standard().
				withRegion(Regions.US_WEST_1).
				withCredentials(new EnvironmentVariableCredentialsProvider()).build();
		String name = Long.toString(System.currentTimeMillis()) + ".ser";
		File temp = new File(name);
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(temp));
			oos.writeObject(tc);
			oos.close();
			PutObjectRequest por = new PutObjectRequest("ohbucketmybucket", "dbManager/" + name, temp);
			client.putObject(por);
		} catch (SdkClientException e) {
			throw new ObjectStorageException("Storage unreachable. Please contact administrator.", e);
		} catch (IOException e) {
			throw new ObjectStorageException("Storage failed due to input", e);
		} catch (Exception e){
			throw new ObjectStorageException("Storage failed for unchecked reason", e);
		} //end try-catch block		
		return name;
	} //end UploadFile
	
	public TableContents GetBucketContents(String filename)
	{
		AmazonS3 client = AmazonS3ClientBuilder.standard().
				withRegion(Regions.US_WEST_1).
				withCredentials(new EnvironmentVariableCredentialsProvider()).build();
		TableContents tc = new TableContents();
		try {
			S3Object s3o = client.getObject(new GetObjectRequest("ohbucketmybucket", "dbManager/" + filename));
			ObjectInputStream ois = new ObjectInputStream((InputStream)s3o.getObjectContent());
			tc = (TableContents)ois.readObject();
		} catch (SdkClientException e) {
			throw new ObjectStorageException("Storage unreachable. Please contact administrator.", e);
		} catch (IOException | ClassNotFoundException e) {
			throw new ObjectStorageException("Error while downloading the file.", e);
		} //end try-catch block
		//client.deleteObject(new DeleteObjectRequest("ohbucketmybucket", "dbManager/" + filename));
		return tc;
	} //end GetBucketContents
} //end S3Manager class
