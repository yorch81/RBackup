package net.yorch.rbackup;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * FMTemplate
 * 
 * Manage FreeMarker Template
 * 
 * Copyright 2015 Jorge Alberto Ponce Turrubiates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * @category   FMTemplate
 * @package    net.yorch.rbackup
 * @copyright  Copyright 2015 JAPT
 * @license    http://www.apache.org/licenses/LICENSE-2.0
 * @version    1.0.0, 2015-01-15
 * @author     <a href="mailto:the.yorch@gmail.com">Jorge Alberto Ponce Turrubiates</a>
 */
public class FMTemplate {
	/**
     * Path and Name of Template
     *
     * @access private
     */
	private String template = "";
	
	/**
     * List of Data in Template
     *
     * @access private
     */
	private Map<String, Object> data = null;
	
	/**
     * Constructor of Class
     * 
     * @param templateName String Path and Name of Template
     * @param templateData Map<String, Object> List of Data in Template
     * @return Instance
     * @see FMTemplate
     */
	public FMTemplate(String templateName, Map<String, Object> templateData) {
		this.template = templateName;
		this.data = templateData;
	}

	/**
     * Return the processed template or null if exists error
     * 
     * @return StringWriter
     */
	public StringWriter get(){
		StringWriter writer = new StringWriter();
		
		Configuration configuration = new Configuration();
		configuration.setClassForTemplateLoading(WebApp.class, "/public");
		
		try {
			Template myTemplate = configuration.getTemplate(this.template);
     		myTemplate.process(this.data, writer);
		} catch (IOException e) {
			writer = null;
			e.printStackTrace();
		} catch (TemplateException e) {
			writer = null;
			e.printStackTrace();
		} 
		
		return writer;
	}

}
