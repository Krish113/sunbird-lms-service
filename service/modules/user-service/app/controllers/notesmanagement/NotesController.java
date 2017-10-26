package controllers.notesmanagement;

import akka.util.Timeout;
import controllers.common.BaseController;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.ActorOperations;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.request.ExecutionContext;
import org.sunbird.common.request.Request;
import org.sunbird.common.request.RequestValidator;
import org.sunbird.common.responsecode.ResponseCode;
import play.libs.F.Promise;
import play.mvc.Result;

/**
 * Controller class to handle Notes related operation such as 
 * create, read/get, search, update and delete
 *
 */
public class NotesController extends BaseController {
  
  /**
   * Method to create Note
   * @return Promise<Result>
   */
  public Promise<Result> createNote() {
    try {
      JsonNode requestData = request().body().asJson();
      ProjectLogger.log("Create note request: " + requestData, LoggerEnum.INFO.name());
      Request reqObj = (Request) mapper.RequestMapper.mapRequest(requestData, Request.class);
      if(null != ctx().flash().get(JsonKey.IS_AUTH_REQ) && Boolean.parseBoolean(ctx().flash().get(JsonKey.IS_AUTH_REQ))){
        String userId = (String) reqObj.get(JsonKey.USER_ID);
        if((!ProjectUtil.isStringNullOREmpty(userId)) && (!userId.equals(ctx().flash().get(JsonKey.USER_ID)))){
            throw new ProjectCommonException(ResponseCode.unAuthorised.getErrorCode(), ResponseCode.unAuthorised.getErrorMessage(), ResponseCode.UNAUTHORIZED.getResponseCode());
        }
      }
      RequestValidator.validateNote(reqObj);
      reqObj.setManagerName(ActorOperations.CREATE_NOTE.getKey());
      reqObj.setOperation(ActorOperations.CREATE_NOTE.getValue());
      reqObj.setRequestId(ExecutionContext.getRequestId());
      reqObj.setEnv(getEnvironment());
      HashMap<String, Object> innerMap = new HashMap<>();
      innerMap.put(JsonKey.NOTE, reqObj.getRequest());
      innerMap.put(JsonKey.REQUESTED_BY,ctx().flash().get(JsonKey.USER_ID));
      reqObj.setRequest(innerMap);
      Timeout timeout = new Timeout(Akka_wait_time, TimeUnit.SECONDS);
      return actorResponseHandler(getRemoteActor(), reqObj, timeout, null, null);
    } catch (Exception e) {
      ProjectLogger.log("Error in controller", e);
      return Promise.<Result>pure(createCommonExceptionResponse(e, null));
    }
  }

  /**
   * Method to update the note
   * @param noteId
   * @return Promise<Result>
   */
  public Promise<Result> updateNote(String noteId) {
    try {
      JsonNode requestData = request().body().asJson();
      ProjectLogger.log("Update note request: " + requestData, LoggerEnum.INFO.name());
      Request reqObj = (Request) mapper.RequestMapper.mapRequest(requestData, Request.class);
      RequestValidator.validateNoteId(noteId);
      reqObj.setManagerName(ActorOperations.UPDATE_NOTE.getKey());
      reqObj.setOperation(ActorOperations.UPDATE_NOTE.getValue());
      reqObj.setRequestId(ExecutionContext.getRequestId());
      reqObj.setEnv(getEnvironment());
      HashMap<String, Object> innerMap = new HashMap<>();
      innerMap.put(JsonKey.NOTE_ID, noteId);
      innerMap.put(JsonKey.NOTE, reqObj.getRequest());
      innerMap.put(JsonKey.REQUESTED_BY,ctx().flash().get(JsonKey.USER_ID));
      reqObj.setRequest(innerMap);
      Timeout timeout = new Timeout(Akka_wait_time, TimeUnit.SECONDS);
      return actorResponseHandler(getRemoteActor(), reqObj, timeout, null, null);
    } catch (Exception e) {
      ProjectLogger.log("Error in controller", e);
      return Promise.<Result>pure(createCommonExceptionResponse(e, null));
    }
  }
  
  /**
   * Method to get the note details
   * @param noteId
   * @return Promise<Result>
   */
  public Promise<Result> getNote(String noteId) {
    try {
      ProjectLogger.log("Get Note request: " + noteId, LoggerEnum.INFO.name());
      RequestValidator.validateNoteId(noteId);
      Request reqObj = new Request();
      reqObj.setManagerName(ActorOperations.GET_NOTE.getKey());
      reqObj.setOperation(ActorOperations.GET_NOTE.getValue());
      reqObj.setRequestId(ExecutionContext.getRequestId());
      reqObj.setEnv(getEnvironment());
      HashMap<String, Object> innerMap = new HashMap<>();
      innerMap.put(JsonKey.REQUESTED_BY,ctx().flash().get(JsonKey.USER_ID));
      innerMap.put(JsonKey.NOTE_ID, noteId);
      reqObj.setRequest(innerMap);

      Timeout timeout = new Timeout(Akka_wait_time, TimeUnit.SECONDS);
      return actorResponseHandler(getRemoteActor(), reqObj, timeout, null, null);
    } catch (Exception e) {
      ProjectLogger.log("Error in controller", e);
      return Promise.<Result>pure(createCommonExceptionResponse(e, null));
    }
  }

  /**
   * Method to search note
   * @return
   */
  public Promise<Result> searchNote() {
    try {
      JsonNode requestData = request().body().asJson();
      ProjectLogger.log("Search Note request: " + requestData, LoggerEnum.INFO.name());
      Request reqObj = (Request) mapper.RequestMapper.mapRequest(requestData, Request.class);
      reqObj.setManagerName(ActorOperations.SEARCH_NOTE.getKey());
      reqObj.setOperation(ActorOperations.SEARCH_NOTE.getValue());
      reqObj.setRequestId(ExecutionContext.getRequestId());
      reqObj.setEnv(getEnvironment());
      HashMap<String, Object> innerMap = new HashMap<>();
      innerMap.put(JsonKey.REQUESTED_BY,ctx().flash().get(JsonKey.USER_ID));
      innerMap.putAll(reqObj.getRequest());
      reqObj.setRequest(innerMap);
      Timeout timeout = new Timeout(Akka_wait_time, TimeUnit.SECONDS);
      return actorResponseHandler(getRemoteActor(), reqObj, timeout, null, null);
    } catch (Exception e) {
      ProjectLogger.log("Error in controller", e);
      return Promise.<Result>pure(createCommonExceptionResponse(e, null));
    }
  }

  /**
   * Method to delete the note
   * @param noteId
   * @return Promise<Result>
   */
  public Promise<Result> deleteNote(String noteId) {
    try {
      ProjectLogger.log("Delete Note request: " + noteId, LoggerEnum.INFO.name());
      Request reqObj = new Request();
      RequestValidator.validateNoteId(noteId);
      reqObj.setManagerName(ActorOperations.DELETE_NOTE.getKey());
      reqObj.setOperation(ActorOperations.DELETE_NOTE.getValue());
      reqObj.setRequestId(ExecutionContext.getRequestId());
      reqObj.setEnv(getEnvironment());
      HashMap<String, Object> innerMap = new HashMap<>();
      innerMap.put(JsonKey.NOTE_ID, noteId);
      innerMap.put(JsonKey.REQUESTED_BY,ctx().flash().get(JsonKey.USER_ID));
      reqObj.setRequest(innerMap);
      Timeout timeout = new Timeout(Akka_wait_time, TimeUnit.SECONDS);
      return actorResponseHandler(getRemoteActor(), reqObj, timeout, null, null);
    } catch (Exception e) {
      ProjectLogger.log("Error in controller", e);
      return Promise.<Result>pure(createCommonExceptionResponse(e, null));
    }
  }
}