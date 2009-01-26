/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.webbeans.context;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.context.Context;

import org.jboss.webbeans.util.ConcurrentCache;

/**
 * A map from a scope to a list of contexts
 * 
 * @author Nicklas Karlsson
 * @author Pete Muir
 * 
 */
public class ContextMap extends ConcurrentCache<Class<? extends Annotation>, List<Context>>
{

   /**
    * Gets the dependent context
    * 
    * @param scopeType The scope type to get
    * @return The dependent context
    */
   public AbstractContext getBuiltInContext(Class<? extends Annotation> scopeType)
   {
      if (getContext(scopeType) != null)
      {
         return (AbstractContext) getContext(scopeType).get(0);
      }
      else
      {
         return null;
      }
   }

   /**
    * Gets the list of context with the given scope type
    * 
    * @param scopeType The scope type to match
    * @return A list of matching contexts. An empty list is returned if there
    *         are no matches
    */
   public List<Context> getContext(Class<? extends Annotation> scopeType)
   {
      List<Context> contexts = getValue(scopeType);
      if (contexts == null)
      {
         return Collections.emptyList();
      }
      else
      {
         return contexts;
      }
   }

   @Override
   public String toString()
   {
      return "ContextMap holding " + delegate().size() + " contexts: " + delegate().keySet();
   }

   /**
    * Adds a context under a scope type
    * 
    * Creates the list of contexts if it doesn't exist
    * 
    * @param context The new context
    */
   public void add(Context context)
   {
      List<Context> contexts = putIfAbsent(context.getScopeType(), new Callable<List<Context>>()
      {

         public List<Context> call() throws Exception
         {
            return new CopyOnWriteArrayList<Context>();
         }

      });
      contexts.add(context);
   }

}