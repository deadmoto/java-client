/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.appium.java_client.pagefactory;

import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.ContextAware;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

class ContentMappedBy extends By {
    private final Map<ContentType, By> map;
    private final static String NATIVE_APP_PATTERN = "NATIVE_APP";
    
	public ContentMappedBy(Map<ContentType, By> map) {
		this.map = map;
	}

	private By returnRelevantBy(SearchContext context){
		WebDriver driver = WebDriverUnpackUtility.unpackWebDriverFromSearchContext(context);
		if (!ContextAware.class.isAssignableFrom(driver.getClass())){ //it is desktop browser 
			return map.get(ContentType.HTML);
		}

		ContextAware contextAware = ContextAware.class.cast(driver);
		String currentContext = contextAware.getContext();
		if (currentContext.contains(NATIVE_APP_PATTERN))
			return map.get(ContentType.NATIVE);
		return map.get(ContentType.HTML);
	}
	
	@Override
	public List<WebElement> findElements(SearchContext context) {
		return context.findElements(returnRelevantBy(context));
	}

    @Override
    public String toString(){
        By defaultBy = map.get(ContentType.HTML);
        By nativeBy  = map.get(ContentType.NATIVE);

        if (defaultBy.equals(nativeBy))
            return defaultBy.toString();

        return  "Locator map: " + "\n" +
                "- native content: \"" + nativeBy.toString() + "\" \n" +
                "- html content: \"" + defaultBy.toString() + "\"";
    }

}
