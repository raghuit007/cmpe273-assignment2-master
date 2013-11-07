package edu.sjsu.cmpe.procurement.config;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.client.JerseyClientConfiguration;
import com.yammer.dropwizard.config.Configuration;

public class ProcurementServiceConfiguration extends Configuration {
    @NotEmpty
    @JsonProperty
    private static String stompQueueName;

    @NotEmpty
    @JsonProperty
    private static String stompTopicName;
    
    @NotEmpty
    @JsonProperty
    private String stompTopicPrefix;

    @NotEmpty
    @JsonProperty
    private static String apolloUser;

    @NotEmpty
    @JsonProperty
    private static String apolloPassword;
    
    @NotEmpty
    @JsonProperty
    private static String apolloHost;
    
    @JsonProperty
    private static int apolloPort;

    @Valid
    @NotNull
    @JsonProperty
    private JerseyClientConfiguration httpClient = new JerseyClientConfiguration();

    /**
     * 
     * @return the httpClient
     */
    public JerseyClientConfiguration getJerseyClientConfiguration() {
	return httpClient;
    }

    /**
     * @return the stompQueueName
     */
    public String getStompQueueName() {
	return stompQueueName;
    }

    /**
     * @param stompQueueName
     *            the stompQueueName to set
     */
    public void setStompQueueName(String stompQueueName) {
            ProcurementServiceConfiguration.stompQueueName = stompQueueName;
    }

     /**
     * @return the stompTopicName
     */
    public String getStompTopicName() {
        return stompTopicName;
    }

    /**
     * @param stompQueueName
     *            the stompQueueName to set
     */
        /**
     * @return the apolloUser
     */
    public String getApolloUser() {
                return apolloUser;
        }
    
    /**
     * @param apolloUser
     *            the apolloUser to set
     */
        public void setApolloUser(String apolloUser) {
                ProcurementServiceConfiguration.apolloUser = apolloUser;
        }
        
        /**
     * @return the apolloPassword
     */
        public String getApolloPassword() {
                return apolloPassword;
        }
        
        /**
     * @param apolloPassword
     *            the apolloPassword to set
     */
        public void setApolloPassword(String apolloPassword) {
                ProcurementServiceConfiguration.apolloPassword = apolloPassword;
        }
        
        /**
     * @return the apolloHost
     */
        public String getApolloHost() {
                return apolloHost;
        }
        
        /**
     * @param apolloHost
     *            the apolloHost to set
     */
        public void setApolloHost(String apolloHost) {
                ProcurementServiceConfiguration.apolloHost = apolloHost;
        }
        
        /**
     * @return the apolloPort
     */
        public int getApolloPort() {
                return apolloPort;
        }
        
        /**
     * @param apolloPort
     *            the apolloPort to set
     */
        public void setApolloPort(int apolloPort) {
                ProcurementServiceConfiguration.apolloPort = apolloPort;
        }

    public String getStompTopicPrefix() {
	return stompTopicPrefix;
    }

    public void setStompTopicPrefix(String stompTopicPrefix) {
	this.stompTopicPrefix = stompTopicPrefix;
    }

}
